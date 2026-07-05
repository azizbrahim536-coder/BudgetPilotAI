import calendar
import json
import os

from collections import defaultdict
from typing import Literal

import requests

from dotenv import load_dotenv
from flask import Flask, jsonify, request
from flask_cors import CORS

from google import genai

from pydantic import BaseModel, Field


load_dotenv()


app = Flask(__name__)

CORS(
    app,
    resources={
        r"/api/*": {
            "origins": [
                "http://localhost:4200",
                "http://127.0.0.1:4200"
            ]
        }
    }
)


GEMINI_API_KEY = os.getenv(
    "GEMINI_API_KEY"
)

GEMINI_MODEL = os.getenv(
    "GEMINI_MODEL",
    "gemini-3.5-flash"
)

SPRING_API = os.getenv(
    "SPRING_API",
    "http://localhost:8081/api"
)

AI_PORT = int(
    os.getenv(
        "AI_PORT",
        "5003"
    )
)


if not GEMINI_API_KEY:
    raise RuntimeError(
        "GEMINI_API_KEY est absent du fichier .env"
    )


client = genai.Client(
    api_key=GEMINI_API_KEY
)


class Recommendation(BaseModel):

    title: str = Field(
        description="Titre court de la recommandation."
    )

    description: str = Field(
        description="Action pratique et facile à comprendre."
    )

    priority: Literal[
        "HIGH",
        "MEDIUM",
        "LOW"
    ] = Field(
        description="Priorité de la recommandation."
    )


class SavingOpportunity(BaseModel):

    category: str = Field(
        description="Catégorie concernée."
    )

    estimated_monthly_saving: float = Field(
        ge=0,
        description="Économie mensuelle estimée en TND."
    )

    action: str = Field(
        description="Action permettant de réaliser cette économie."
    )


class FinancialAnalysis(BaseModel):

    summary: str = Field(
        description="Résumé global de la situation financière."
    )

    financial_health: Literal[
        "GOOD",
        "WARNING",
        "CRITICAL"
    ] = Field(
        description="État de santé financière."
    )

    score: int = Field(
        ge=0,
        le=100,
        description="Score financier compris entre 0 et 100."
    )

    key_observations: list[str] = Field(
        description="Observations importantes basées sur les données."
    )

    recommendations: list[Recommendation] = Field(
        description="Liste de recommandations pratiques."
    )

    saving_opportunities: list[SavingOpportunity] = Field(
        description="Opportunités réalistes pour économiser."
    )

    next_month_target: str = Field(
        description="Objectif simple pour le mois suivant."
    )

    disclaimer: str = Field(
        description="Avertissement indiquant que ceci ne remplace pas un conseiller financier."
    )


def fetch_backend_data(
    endpoint: str,
    params: dict | None = None
):
    """
    Envoie une requête GET au backend Spring Boot.
    """

    url = f"{SPRING_API}{endpoint}"

    response = requests.get(
        url,
        params=params,
        timeout=15
    )

    response.raise_for_status()

    return response.json()


def get_period_dates(
    year: int,
    month: int
) -> tuple[str, str]:
    """
    Retourne le premier et le dernier jour du mois.
    """

    last_day = calendar.monthrange(
        year,
        month
    )[1]

    start_date = (
        f"{year}-"
        f"{month:02d}-"
        "01"
    )

    end_date = (
        f"{year}-"
        f"{month:02d}-"
        f"{last_day:02d}"
    )

    return start_date, end_date


def calculate_category_totals(
    transactions: list[dict]
) -> tuple[dict, dict]:
    """
    Calcule les revenus et dépenses par catégorie.
    """

    incomes = defaultdict(float)
    expenses = defaultdict(float)

    for transaction in transactions:

        amount = float(
            transaction.get(
                "amount",
                0
            )
        )

        transaction_type = transaction.get(
            "type"
        )

        category = (
            transaction.get(
                "category"
            )
            or {}
        )

        category_name = category.get(
            "name",
            "Autre"
        )

        if transaction_type == "INCOME":
            incomes[category_name] += amount

        elif transaction_type == "EXPENSE":
            expenses[category_name] += amount

    sorted_incomes = dict(
        sorted(
            incomes.items(),
            key=lambda item: item[1],
            reverse=True
        )
    )

    sorted_expenses = dict(
        sorted(
            expenses.items(),
            key=lambda item: item[1],
            reverse=True
        )
    )

    return sorted_incomes, sorted_expenses


def build_budget_context(
    budgets: list[dict],
    expenses_by_category: dict
) -> list[dict]:
    """
    Calcule la progression de chaque budget.
    """

    results = []

    for budget in budgets:

        category = (
            budget.get(
                "category"
            )
            or {}
        )

        category_name = category.get(
            "name",
            "Autre"
        )

        limit_amount = float(
            budget.get(
                "limitAmount",
                0
            )
        )

        spent_amount = float(
            expenses_by_category.get(
                category_name,
                0
            )
        )

        remaining_amount = (
            limit_amount -
            spent_amount
        )

        usage_percentage = 0

        if limit_amount > 0:
            usage_percentage = round(
                (
                    spent_amount /
                    limit_amount
                ) * 100,
                2
            )

        results.append({
            "category": category_name,
            "limitAmount": limit_amount,
            "spentAmount": spent_amount,
            "remainingAmount": remaining_amount,
            "usagePercentage": usage_percentage,
            "exceeded": (
                spent_amount >
                limit_amount
            )
        })

    return results


def get_largest_transactions(
    transactions: list[dict],
    limit: int = 5
) -> list[dict]:
    """
    Retourne les dépenses les plus importantes.
    """

    expenses = [
        transaction
        for transaction in transactions
        if transaction.get("type") == "EXPENSE"
    ]

    expenses.sort(
        key=lambda transaction: float(
            transaction.get(
                "amount",
                0
            )
        ),
        reverse=True
    )

    results = []

    for transaction in expenses[:limit]:

        category = (
            transaction.get(
                "category"
            )
            or {}
        )

        results.append({
            "title": transaction.get(
                "title",
                "Transaction"
            ),
            "amount": float(
                transaction.get(
                    "amount",
                    0
                )
            ),
            "category": category.get(
                "name",
                "Autre"
            ),
            "date": transaction.get(
                "transactionDate"
            )
        })

    return results


def build_financial_context(
    year: int,
    month: int
) -> dict:
    """
    Récupère et prépare les données financières.
    """

    start_date, end_date = get_period_dates(
        year,
        month
    )

    statistics = fetch_backend_data(
        "/statistics/dashboard",
        {
            "year": year,
            "month": month
        }
    )

    transactions = fetch_backend_data(
        "/transactions",
        {
            "dateFrom": start_date,
            "dateTo": end_date
        }
    )

    budgets = fetch_backend_data(
        "/budgets",
        {
            "year": year,
            "month": month
        }
    )

    (
        incomes_by_category,
        expenses_by_category
    ) = calculate_category_totals(
        transactions
    )

    budget_context = build_budget_context(
        budgets,
        expenses_by_category
    )

    largest_expenses = get_largest_transactions(
        transactions
    )

    return {
        "period": {
            "year": year,
            "month": month,
            "startDate": start_date,
            "endDate": end_date
        },

        "statistics": statistics,

        "incomeByCategory":
            incomes_by_category,

        "expenseByCategory":
            expenses_by_category,

        "budgets":
            budget_context,

        "largestExpenses":
            largest_expenses,

        "transactionCount":
            len(transactions)
    }


def build_prompt(
    financial_context: dict,
    language: str
) -> str:
    """
    Crée le prompt envoyé à Gemini.
    """

    language_instruction = (
        "Réponds entièrement en français."
    )

    if language == "ar":
        language_instruction = (
            "أجب بالكامل باللغة العربية الواضحة."
        )

    return f"""
Tu es un assistant spécialisé dans l'analyse
de budgets personnels.

Analyse uniquement les données financières fournies.
N'invente aucune transaction ou valeur.

Devise utilisée : TND, dinar tunisien.

Tes recommandations doivent être :
- pratiques ;
- réalistes ;
- courtes ;
- directement liées aux données ;
- sans promesse de résultat financier.

{language_instruction}

Règles pour le score :
- 80 à 100 : situation saine ;
- 55 à 79 : situation correcte mais améliorable ;
- 30 à 54 : situation fragile ;
- 0 à 29 : situation critique.

Prends en compte :
- le rapport entre revenus et dépenses ;
- le solde mensuel ;
- les catégories les plus coûteuses ;
- les budgets dépassés ;
- les opportunités réalistes d'économie ;
- les dépenses les plus importantes.

Données financières :

{json.dumps(
    financial_context,
    ensure_ascii=False,
    indent=2
)}
"""


@app.get("/health")
def health():
    """
    Vérifie que le service IA fonctionne.
    """

    return jsonify({
        "status": "UP",
        "service": "BudgetPilot AI Service",
        "model": GEMINI_MODEL
    })


@app.post("/api/ai/analyze")
def analyze_finances():
    """
    Analyse les finances d'un mois avec Gemini.
    """

    try:
        body = request.get_json(
            silent=True
        ) or {}

        year = body.get("year")
        month = body.get("month")
        language = body.get(
            "language",
            "fr"
        )

        if not isinstance(year, int):
            return jsonify({
                "message":
                    "L'année doit être un nombre entier."
            }), 400

        if not isinstance(month, int):
            return jsonify({
                "message":
                    "Le mois doit être un nombre entier."
            }), 400

        if year < 2000 or year > 2100:
            return jsonify({
                "message":
                    "L'année est invalide."
            }), 400

        if month < 1 or month > 12:
            return jsonify({
                "message":
                    "Le mois doit être compris entre 1 et 12."
            }), 400

        if language not in [
            "fr",
            "ar"
        ]:
            language = "fr"

        financial_context = (
            build_financial_context(
                year,
                month
            )
        )

        if (
            financial_context[
                "transactionCount"
            ] == 0
        ):
            return jsonify({
                "summary":
                    "Aucune transaction disponible pour cette période.",

                "financial_health":
                    "WARNING",

                "score":
                    0,

                "key_observations": [
                    "Aucune donnée financière ne permet de réaliser une analyse."
                ],

                "recommendations": [
                    {
                        "title":
                            "Ajouter des transactions",

                        "description":
                            "Enregistrez vos revenus et vos dépenses pour obtenir une analyse personnalisée.",

                        "priority":
                            "HIGH"
                    }
                ],

                "saving_opportunities":
                    [],

                "next_month_target":
                    "Enregistrer régulièrement les revenus et les dépenses.",

                "disclaimer":
                    "Cette analyse est informative et ne remplace pas un conseiller financier."
            })

        prompt = build_prompt(
            financial_context,
            language
        )

        interaction = (
            client.interactions.create(
                model=GEMINI_MODEL,

                input=prompt,

                response_format={
                    "type": "text",

                    "mime_type":
                        "application/json",

                    "schema":
                        FinancialAnalysis
                        .model_json_schema()
                }
            )
        )

        analysis = (
            FinancialAnalysis
            .model_validate_json(
                interaction.output_text
            )
        )

        return jsonify(
            analysis.model_dump()
        )

    except requests.exceptions.ConnectionError:
        return jsonify({
            "message":
                "Connexion au backend Spring Boot impossible."
        }), 503

    except requests.exceptions.Timeout:
        return jsonify({
            "message":
                "Le backend Spring Boot ne répond pas."
        }), 504

    except requests.exceptions.HTTPError as error:
        return jsonify({
            "message":
                "Le backend Spring Boot a retourné une erreur.",

            "details":
                str(error)
        }), 502

    except Exception as error:
        print(
            "Erreur IA :",
            error
        )

        return jsonify({
            "message":
                "Impossible de générer l'analyse financière.",

            "details":
                str(error)
        }), 500


if __name__ == "__main__":
    app.run(
        host="0.0.0.0",
        port=AI_PORT,
        debug=True
    )