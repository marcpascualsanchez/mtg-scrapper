function flagValidPrices(cardName) {
    var aElements = document.querySelectorAll('.col-sellerProductInfo a');

    // Loop through the <a> elements
    for (var elementId = 0; elementId < aElements.length; elementId++) {
        var aElement = aElements[elementId];

        // Check if the text content of the <a> element is the cardName
        if (aElement.textContent === cardName) {
            // Traverse up the DOM to the row container
            var rowContainer = aElement.closest('.row');

            // Find the price container and relate it with the row container
            var priceContainer = rowContainer.querySelector('.color-primary');
            priceContainer.classList.add("mtg-scrapper-valid-price")
            priceContainer.setAttribute("mtg-scrapper-id", elementId.toString())

            // Find the amount container and relate it with the row container
            var amountContainer = rowContainer.querySelector('.item-count');
            amountContainer.classList.add("mtg-scrapper-valid-amount")
            amountContainer.setAttribute("mtg-scrapper-id", elementId.toString())
        }
    }
}