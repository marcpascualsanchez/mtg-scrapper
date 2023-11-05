Feature: API POST evaluates card list by seller

  Scenario: cards are found and a CSV is returned
    Given cardmarket website responses are
      | cardName              | seller         | response                            |
      | Bloodthirsty Blade    | MagicBarcelona | cheapBloodthirstyBladeResponse.html |
      | Bloodthirsty Blade    | inGenio        | notFoundCard.html                   |
      | Pendant of Prosperity | MagicBarcelona | cheapPendantOfProsperity.html       |
      | Pendant of Prosperity | inGenio        | expensivePendantOfProsperity.html   |
    When a POST is requested with body "baseRequest.json"
    Then the response is a csv matching "allFoundCards.csv"

  Scenario: cards are found in multiple sellers to cover the exactly amount requested and a CSV is returned
    Given cardmarket website responses are
      | cardName              | seller         | response                            |
      | Bloodthirsty Blade    | MagicBarcelona | cheapBloodthirstyBladeResponse.html |
      | Bloodthirsty Blade    | inGenio        | notFoundCard.html                   |
      | Pendant of Prosperity | MagicBarcelona | cheapPendantOfProsperity.html       |
      | Pendant of Prosperity | inGenio        | expensivePendantOfProsperity.html   |
    When a POST is requested with body "exactAmountFromMultipleSellersRequest.json"
    Then the response is a csv matching "allFoundCardsFromMultipleSellers.csv"

  Scenario: cards are found in multiple sellers but are less than amount requested and a CSV is returned
    Given cardmarket website responses are
      | cardName              | seller         | response                            |
      | Bloodthirsty Blade    | MagicBarcelona | cheapBloodthirstyBladeResponse.html |
      | Bloodthirsty Blade    | inGenio        | notFoundCard.html                   |
      | Pendant of Prosperity | MagicBarcelona | cheapPendantOfProsperity.html       |
      | Pendant of Prosperity | inGenio        | expensivePendantOfProsperity.html   |
    When a POST is requested with body "tooMuchAmountFromMultipleSellersRequest.json"
    Then the response is a csv matching "partiallyFoundCardsFromMultipleSellers.csv"

  Scenario: cards are not found and a CSV is returned
    Given cardmarket website responses are
      | cardName              | seller         | response          |
      | Bloodthirsty Blade    | MagicBarcelona | notFoundCard.html |
      | Bloodthirsty Blade    | inGenio        | notFoundCard.html |
      | Pendant of Prosperity | MagicBarcelona | notFoundCard.html |
      | Pendant of Prosperity | inGenio        | notFoundCard.html |
    When a POST is requested with body "baseRequest.json"
    Then the response is a csv matching "allNotFoundCards.csv"

  Scenario: a card and its art series are both found and a CSV is returned
    Given cardmarket website responses are
      | cardName         | seller         | response              |
      | Answered Prayers | MagicBarcelona | artSeriesAndCard.html |
      | Answered Prayers | inGenio        | notFoundCard.html     |
    When a POST is requested with body "artSeriesAmbiguityRequest.json"
    Then the response is a csv matching "artSeriesAmbiguity.csv"

  Scenario: only the art series of a card is found and a CSV is returned
    Given cardmarket website responses are
      | cardName         | seller         | response              |
      | Cyclone Summoner | MagicBarcelona | artSeriesOnly.html |
      | Cyclone Summoner | inGenio        | notFoundCard.html     |
    When a POST is requested with body "artSeriesOnlyRequest.json"
    Then the response is a csv matching "artSeriesOnly.csv"

  # TODO
    # could we also add the option for add-to-cart? -> sign in with credentials + add-to-cart click
    # works with name containing colon or weird chars (NTH)
    # foils
    # accept cookies at initialization?
    # handle selenium close