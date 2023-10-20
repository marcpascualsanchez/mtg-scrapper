Feature: API POST evaluates card list by seller

  Scenario: correct request returns CSV
    # given mock cardmarket pages
    When a POST is received with body "allCorrectCardsList.json"
  # when receive post -> evaluation of each card (jsoup like puppeteer)
    # could we also add the option for add-to-cart?