// instantiate the bloodhound suggestion engine

var matchingTitles = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
        url: BASE_URL + '/search/title?qt=%QUERY',
        wildcard: '%QUERY'
      }
});

matchingTitles.initialize();

$(".typeahead").typeahead({
    hint: true,
    highlight: true,
    minLength: 1
},
{
    name: 'title-search',
    displayKey: 'value',
    items: 10,
    source: matchingTitles.ttAdapter()
});

$(".typeahead").keydown(function(e) {
    if (e.keyCode === 13) {
        $('#search').submit();
    }
});

function askMore() {
    if (!$("#more-ajax-cont")) {
        return;
    }
    var searchTerm = $("#hid-title").val();
    $.ajax({
        url: BASE_URL + "/search/more?t=" + searchTerm,
        dataType: "html",
        error: function() {
            console.log("Can not get the result");
        },
        success: function(response) {
            console.log(response);
            if (response) {
                $("#more-ajax-cont").html(response);
            } else {
                console.log("No search result");
            }
        }
    });
}

askMore();
