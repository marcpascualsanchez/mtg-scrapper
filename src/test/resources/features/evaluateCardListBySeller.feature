Feature: API POST evaluates card list by seller

  Scenario: correct request returns CSV
    When a POST is received with body "allCorrectCardsList.json"
  # given mock cardmarket pages
  # when receive post -> evaluation of each card (jsoup like puppeteer)
    # could we also add the option for add-to-cart?