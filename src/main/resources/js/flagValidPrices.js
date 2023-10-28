function flagValidPrices(cardName) {
    var aElements = document.querySelectorAll('.col-sellerProductInfo a');

    // Loop through the <a> elements
    for (var i = 0; i < aElements.length; i++) {
        var aElement = aElements[i];

        // Check if the text content of the <a> element is the cardName
        if (aElement.textContent === cardName) {
            // Traverse up the DOM to the row container
            var rowContainer = aElement.closest('.row');

            // Find the price container within the row container
            var priceContainer = rowContainer.querySelector('.color-primary');
            priceContainer.classList.add("mtg-scrapper-valid-price")
        }
    }
}