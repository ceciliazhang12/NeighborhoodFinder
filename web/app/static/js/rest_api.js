// TODO: Js code to get value from eco slider

function showValue(newValue)
{
  document.getElementById("range").innerHTML=newValue;
}
var map;
  function initMap(locations) {
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 5,
      center: new google.maps.LatLng(37.09024, -95.712891),
      mapTypeId: 'roadmap'
    });

    /*
    var features = [];
    */

    for (location of locations) {
        print(location[0])
        print(location[0])
        print(location[1])
        print(location[1])
        /*
        features.push({
            position: new google.maps.LatLng(location)
        })
        */
    }

    var features = [
      {
        position: new google.maps.LatLng(34.0522342, -118.24368489999999)
      }, {
        position: new google.maps.LatLng(40.7127753, -74.0059728)
      }
    ];

    // Create markers.
    features.forEach(function(feature) {
      var marker = new google.maps.Marker({
        position: feature.position,
        map: map
      });
    });
  }

/*
var map;
  function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 16,
      center: new google.maps.LatLng(-33.91722, 151.23064),
      mapTypeId: 'roadmap'
    });

    var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
    var icons = {
      parking: {
        icon: iconBase + 'parking_lot_maps.png'
      },
      library: {
        icon: iconBase + 'library_maps.png'
      },
      info: {
        icon: iconBase + 'info-i_maps.png'
      }
    };

    var features = [
      {
        position: new google.maps.LatLng(-33.91721, 151.22630),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91539, 151.22820),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91747, 151.22912),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91910, 151.22907),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91725, 151.23011),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91872, 151.23089),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91784, 151.23094),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91682, 151.23149),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91790, 151.23463),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91666, 151.23468),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.916988, 151.233640),
        type: 'info'
      }, {
        position: new google.maps.LatLng(-33.91662347903106, 151.22879464019775),
        type: 'parking'
      }, {
        position: new google.maps.LatLng(-33.916365282092855, 151.22937399734496),
        type: 'parking'
      }, {
        position: new google.maps.LatLng(-33.91665018901448, 151.2282474695587),
        type: 'parking'
      }, {
        position: new google.maps.LatLng(-33.919543720969806, 151.23112279762267),
        type: 'parking'
      }, {
        position: new google.maps.LatLng(-33.91608037421864, 151.23288232673644),
        type: 'parking'
      }, {
        position: new google.maps.LatLng(-33.91851096391805, 151.2344058214569),
        type: 'parking'
      }, {
        position: new google.maps.LatLng(-33.91818154739766, 151.2346203981781),
        type: 'parking'
      }, {
        position: new google.maps.LatLng(-33.91727341958453, 151.23348314155578),
        type: 'library'
      }
    ];

    // Create markers.
    features.forEach(function(feature) {
      var marker = new google.maps.Marker({
        position: feature.position,
        icon: icons[feature.type].icon,
        map: map
      });
    });
  }











/*
$(function () {
    // ****************************************
    //  U T I L I T Y   F U N C T I O N S
    // ****************************************

    // Updates the form with data from the response
    function update_form_data(res) {
        $("#inventory_id").val(res.id);
        $("#inventory_name").val(res.name);
        $("#inventory_quantity").val(res.quantity.toString());
        $("#inventory_status").val(res.status);
    }

    /// Clears all form fields
    function clear_form_data() {
        $("#inventory_name").val("");
        $("#inventory_quantity").val("");
        $("#inventory_status").val("");
    }

    // Updates the flash message area
    function flash_message(message) {
        $("#flash_message").empty();
        $("#flash_message").append(message);
    }

    // ****************************************
    // Create a inventory
    // ****************************************

    $("#create-btn").click(function () {

        var name = $("#inventory_name").val();
        var quantity = parseInt($("#inventory_quantity").val());
        var status = $("#inventory_status").val();

        var data = {
            "name": name,
            "quantity": quantity,
            "status": status
        };

        var ajax = $.ajax({
            type: "POST",
            url: "/inventories",
            contentType:"application/json",
            data: JSON.stringify(data),
        });

        ajax.done(function(res){
            update_form_data(res)
            flash_message("Success")
        });

        ajax.fail(function(res){
            flash_message(res.responseJSON.message)
        });
    });


    // ****************************************
    // Update a inventory
    // ****************************************

    $("#update-btn").click(function () {

        var inventory_id = $("#inventory_id").val();
        var name = $("#inventory_name").val();
        var quantity = parseInt($("#inventory_quantity").val());
        var status = $("#inventory_status").val();

        var data = {
            "name": name,
            "quantity": quantity,
            "status": status
        };

        var ajax = $.ajax({
                type: "PUT",
                url: "/inventories/" + inventory_id,
                contentType:"application/json",
                data: JSON.stringify(data)
            })

        ajax.done(function(res){
            update_form_data(res)
            flash_message("Success")
        });

        ajax.fail(function(res){
            flash_message(res.responseJSON.message)
        });

    });

    // ****************************************
    // Retrieve a inventory
    // ****************************************

    $("#retrieve-btn").click(function () {

        var inventory_id = $("#inventory_id").val();

        var ajax = $.ajax({
            type: "GET",
            url: "/inventories/" + inventory_id,
            contentType:"application/json",
            data: ''
        })

        ajax.done(function(res){
            //alert(res.toSource())
            update_form_data(res)
            flash_message("Success")
        });

        ajax.fail(function(res){
            clear_form_data()
            flash_message(res.responseJSON.message)
        });

    });

    // ****************************************
    // Delete a inventory
    // ****************************************

    $("#delete-btn").click(function () {

        var inventory_id = $("#inventory_id").val();

        var ajax = $.ajax({
            type: "DELETE",
            url: "/inventories/" + inventory_id,
            contentType:"application/json",
            data: '',
        })

        ajax.done(function(res){
            clear_form_data()
            flash_message("inventory with ID [" + inventory_id + "] has been Deleted!")
        });

        ajax.fail(function(res){
            flash_message("Server error!")
        });
    });

    // ****************************************
    // Clear the form
    // ****************************************

    $("#clear-btn").click(function () {
        $("#inventory_id").val("");
        clear_form_data()
    });

    // ****************************************
    // Search for a inventory
    // ****************************************

    $("#search-btn").click(function () {

        var name = $("#inventory_name").val();
        var quantity = $("#inventory_quantity").val();
        var status = $("#inventory_status").val();

        var queryString = ""

        if (name) {
            queryString += 'name=' + name
        }
        if (quantity) {
            if (queryString.length > 0) {
                queryString += '&quantity=' + quantity
            } else {
                queryString += 'quantity=' + quantity
            }
        }
        if (status) {
            if (queryString.length > 0) {
                queryString += '&status=' + status
            } else {
                queryString += 'status=' + status
            }
        }

        var ajax = $.ajax({
            type: "GET",
            url: "/inventories?" + queryString,
            contentType:"application/json",
            data: ''
        })

        ajax.done(function(res){
            //alert(res.toSource())
            $("#search_results").empty();
            $("#search_results").append('<table class="table-striped">');
            var header = '<tr>'
            header += '<th style="width:10%">ID</th>'
            header += '<th style="width:40%">Name</th>'
            header += '<th style="width:40%">Quantity</th>'
            header += '<th style="width:10%">status</th></tr>'
            $("#search_results").append(header);
            for(var i = 0; i < res.length; i++) {
                inventory = res[i];
                var row = "<tr><td>"+inventory.id+"</td><td>"+inventory.name+"</td><td>"+inventory.quantity+"</td><td>"+inventory.status+"</td></tr>";
                $("#search_results").append(row);
            }

            $("#search_results").append('</table>');

            flash_message("Success")
        });

        ajax.fail(function(res){
            flash_message(res.responseJSON.message)
        });

    });

    // ****************************************
    // List all inventories
    // ****************************************

    $("#list-btn").click(function () {

        var ajax = $.ajax({
            type: "GET",
            url: "/inventories",
            contentType:"application/json",
            data: ''
        })

        ajax.done(function(res){
            //alert(res.toSource())
            $("#search_results").empty();
            $("#search_results").append('<table class="table-striped">');
            var header = '<tr>'
            header += '<th style="width:10%">ID</th>'
            header += '<th style="width:40%">Name</th>'
            header += '<th style="width:40%">Quantity</th>'
            header += '<th style="width:10%">status</th></tr>'
            $("#search_results").append(header);
            for(var i = 0; i < res.length; i++) {
                inventory = res[i];
                var row = "<tr><td>"+inventory.id+"</td><td>"+inventory.name+"</td><td>"+inventory.quantity+"</td><td>"+inventory.status+"</td></tr>";
                $("#search_results").append(row);
            }

            $("#search_results").append('</table>');

            flash_message("Success")
        });

        ajax.fail(function(res){
            flash_message(res.responseJSON.message)
        });

    });

    // ****************************************
    // Query the quantity of an inventory
    // ****************************************

    $("#query-btn").click(function () {

        var name = $("#inventory_name").val();
        var quantity = $("#inventory_quantity").val();
        var status = $("#inventory_status").val();

        var queryString = ""

        if (name) {
            queryString += 'name=' + name
        }
        if (status) {
            if (queryString.length > 0) {
                queryString += '&status=' + status
            } else {
                queryString += 'status=' + status
            }
        }

        var ajax = $.ajax({
            type: "GET",
            url: "/inventories/query?" + queryString,
            contentType:"application/json",
            data: ''
        })

        ajax.done(function(res){
            //alert(res.toSource())
            $("#search_results").empty();
            $("#search_results").append('<table class="table-striped">');
            var header = '<tr>'
            header += '<th style="width:10%">ID</th>'
            header += '<th style="width:40%">Name</th>'
            header += '<th style="width:40%">Quantity</th>'
            header += '<th style="width:10%">status</th></tr>'
            $("#search_results").append(header);
            for(var i = 0; i < res.length; i++) {
                inventory = res[i];
                var row = "<tr><td>"+inventory.id+"</td><td>"+inventory.name+"</td><td>"+inventory.quantity+"</td><td>"+inventory.status+"</td></tr>";
                $("#search_results").append(row);
            }

            $("#search_results").append('</table>');

            flash_message("Success")
        });

        ajax.fail(function(res){
            flash_message(res.responseJSON.message)
        });

    });

})
*/
