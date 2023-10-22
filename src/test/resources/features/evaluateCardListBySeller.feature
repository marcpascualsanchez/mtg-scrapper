Feature: API POST evaluates card list by seller

  Scenario: correct request returns CSV
    Given cardmarket website responses are
      | cardName              | seller         | response                            |
      | Bloodthirsty Blade    | MagicBarcelona | cheapBloodthirstyBladeResponse.html |
      | Bloodthirsty Blade    | inGenio        | notFoundCard.html                   |
      | Pendant of Prosperity | MagicBarcelona | cheapPendantOfProsperity.html       |
      | Pendant of Prosperity | inGenio        | expensivePendantOfProsperity.html   |
    When a POST is received with body "allCorrectCardsList.json"
  # when receive post -> evaluation of each card (jsoup like puppeteer)
    # could we also add the option for add-to-cart?