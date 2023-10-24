Feature: API POST evaluates card list by seller

  Scenario: correct request returns CSV
    Given cardmarket website responses are
      | cardName              | seller         | response                            |
      | Bloodthirsty Blade    | MagicBarcelona | cheapBloodthirstyBladeResponse.html |
      | Bloodthirsty Blade    | inGenio        | notFoundCard.html                   |
      | Pendant of Prosperity | MagicBarcelona | cheapPendantOfProsperity.html       |
      | Pendant of Prosperity | inGenio        | expensivePendantOfProsperity.html   |
    When a POST is received with body "allCorrectCardsList.json"
    Then the response is a csv matching "example.csv"


    # could we also add the option for add-to-cart?
    # ensure card name is exactly the same (not one name containing another)