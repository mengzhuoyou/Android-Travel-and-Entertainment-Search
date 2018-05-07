// Init
/*window.onload = function() {
	document.getElementById("submit").disabled = false;
}*/

// Form Location
var Loc = document.JForm.Loc;
Loc[0].onclick = function(){
	Loc[2].setAttribute("disabled",true);
		Loc[2].setAttribute("required",false);
	};
Loc[1].onclick = function(){
	Loc[2].removeAttribute("disabled");
	//Loc[2].value = "";
	Loc[2].setAttribute("required",true);
};

// usc location, in case of fail in ip-api
var usclat = 34.0223519;
var usclon = -118.285117


// Autocomplete
function initAutoC(){
	// Create the autocomplete object, restricting the search to geographical
	// location types.
	autocomplete = new google.maps.places.Autocomplete(
	    (document.getElementById('autocomplete')),
	    {types: ['geocode']}
	);
  autocomplete2 = new google.maps.places.Autocomplete(
          (document.getElementById('inputFrom')),
          {types: ['geocode']}
      );
}


// Location
var lat, lon;


//AngularJS
var sp = angular.module('SearchPage', ['ngAnimate']);

sp.controller('SearchPageController', function($scope, $http, $element) {
  $scope.formData = {};
  $scope.Cats = ['Default', 'Airport', 'Amusement Park', 'Aquarium', 'Art Gallery', 'Bakery', 
                'Bar', 'Beauty Salon', 'Bowling Alley', 'Bus Station', 'Cafe', 'Campground', 
                'Car Rental', 'Casino', 'Lodging', 'Movie Theater', 'Museum', 'Night Club', 
                'Park', 'Parking', 'Restaurant', 'Shopping Mall', 'Stadium', 'Subway Station', 
                'Taxi Stand', 'Train Station', 'Transit Station', 'Travel Agency', 'Zoo'];
  $scope.Details = {};
  $scope.Favorites=[];

  init();
  function init(){
    $scope.showDetails = false;
    $scope.showOneDetail = false;
    $scope.AllDetails = new Array();;
    $scope.hasNext = false;
    $scope.idxD = 0;
    $scope.isF = false;
    $scope.NoRecord = false;
    $scope.Error = false;
    $scope.Switch = false;
    $scope.Detail = [];
    $scope.Detail.length = 0;
    initTabs1();
    initTabs2();
    //set tab
  }
  function initTabs1(){
    $(".tabs").removeClass('active');
    $(".tabs").removeClass('show');
    $(".tab-panes").removeClass('active');
    $(".tab-panes").removeClass('show');
    if (document.getElementById("res"))
      document.getElementById("res").className += " active";
    if (document.getElementById("tab-res"))
      document.getElementById("tab-res").className += " active";
  }

  function initTabs2(){
    $(".showOneDetail .tabs").removeClass('active');
    $(".showOneDetail .tabs").removeClass('show');
    $(".showOneDetail .tab-panes").removeClass('active');
    $(".showOneDetail .tab-panes").removeClass('show');
    if (document.getElementById("tab1"))
      document.getElementById("tab1").className += " active";
    if (document.getElementById("tab-pane1"))
      document.getElementById("tab-pane1").className += " active";
    document.getElementById("RewBtn").innerHTML= "Google reviews";
    document.getElementById("RatBtn").innerHTML= "Default Order";
  }
 

  // get Here
  $scope.checkHere = function LocHere() {
    $scope.formData.LocOp = true;
    $http({
      method: 'GET',
      url: 'http://ip-api.com/json',
      timeout: 1*60 * 1000
    }).then(function (response){
      data = response.data;
      lat = data.lat;
      lon = data.lon;
      //console.log(lat+"+"+lon);
      $scope.gotLoc = true;
    },function (error){
      console.log("Ip-api Website Error");
      $scope.gotLoc = false;
    });
  };

  // Clear
  $scope.reset = function (form){
    $scope.formData = {};
    $scope.formData.Cat = $scope.Cats[0];
    $scope.formData.LocOp = true;
    form.Key.$touched = false
    form.Loc.$touched = false; 
    form.LocOp = false;  
    var Loc = document.JForm.Loc;
    Loc[2].setAttribute("disabled","true");
    init();
  }
  
  // Search
  $scope.search = function(){
    $("#progress").css('display','block');
    init();
    if ($scope.formData.LocOp)
      $scope.inputFrom = "Your location";
    else
      $scope.inputFrom = $scope.formData.Loc;
    //$scope.formData.type = 
    $.ajax({
      type: "GET",
      url: "/search",
      dataTpye: "json",
      data: $scope.formData,
      success: function(data){
        $scope.Details = JSON.parse(data);
        lat =  $scope.Details[1];
        lon =  $scope.Details[2];
        $scope.Details =  $scope.Details[0];
        $scope.showDetails = true;
        if ($scope.Details.results.length == 0)
          $scope.NoRecord = true;
        else
          $scope.NoRecord = false;
        if ($scope.Details.next_page_token){
          $scope.hasNext = true;
          $scope.AllDetails.push($scope.Details);
        }
        else
          $scope.hasNext = false;
        $scope.$apply();
        //alert(JSON.stringify($scope.Details.results[0]));
        //alert($scope.Details.results[0].geometry.location.lat);
        $("#progress").css('display','none');
      },
      error : function(){
        console.log("Search Function Error");
        $scope.Error = true;
        $("#progress").css('display','none');
        $scope.$apply();
      }
    });
  };

  $scope.nextD = function(){
    if ($scope.idxD+1 < $scope.AllDetails.length){
      $scope.idxD = $scope.idxD + 1;
      $scope.Details = $scope.AllDetails[$scope.idxD];
      $scope.hasNext = true;
    }
    else{
      $.ajax({
        type: "GET",
        url: "/search/next",
        data: {'next_page_token': $scope.AllDetails[$scope.idxD].next_page_token},
        success: function(data){
          $scope.Details = JSON.parse(data);
          if ($scope.Details.next_page_token){
            $scope.hasNext = true;
            $scope.AllDetails.push($scope.Details);
          }else
            $scope.hasNext = false;
          $scope.idxD = $scope.idxD + 1;
          $scope.$apply();
        },
        error : function(){
          console.log("NextD Function Error");
          $scope.Error = true;
          $("#progress").css('display','none');
          $scope.$apply();
        }
      });
    }
  };

  $scope.preD = function(){
    $scope.idxD = $scope.idxD - 1;
    $scope.Details = $scope.AllDetails[$scope.idxD];
    $scope.hasNext = true;
  };

  $scope.back = function(){
    $scope.showOneDetail = false;
    $scope.showDetails = true;
    $scope.Switch = true;
    initTabs2();
  }

  var map, toP, marker, streetview, panorama;
  var placeAll;
  $scope.getD = function(idx, isF, isP){
    document.getElementById('route-panel').innerHTML = "";
    initTabs2();
    $scope.Switch = false;
    var Detail;
    if (isP && isP==true){
      Detail = $scope.Detail;
    }
    else{
      if (isF)
        Detail= $scope.Favorites[idx];
      else
        Detail= $scope.Details.results[idx];
      $scope.Detail = Detail;
      $scope.Detail.length = Object.keys(Detail).length;
    }

    // map
    toP = {lat: Detail.geometry.location.lat, lng: Detail.geometry.location.lng};
    map = new google.maps.Map(document.getElementById('map'), {
          center: toP,
          zoom: 15
        });
    marker = new google.maps.Marker({
        position: toP,
        map: map
    });
    // streetview
    panorama = new google.maps.StreetViewPanorama(
        document.getElementById('streetview'), {
          position: toP,
          pov: {
            heading: 34,
            pitch: 10
          }
        });
    map.setStreetView(panorama);

    var request = {
      placeId: Detail.place_id
    };
    service = new google.maps.places.PlacesService(map);
    service.getDetails(request, callback);
    function callback(place, status) {
      if (status === google.maps.places.PlacesServiceStatus.OK) {
            placeAll = place;
           
            //info
            $scope.serviceD = {};
            $scope.serviceD.place_id = place.place_id;
            if (place.name)
              $scope.serviceD.name = place.name;
            if (place.formatted_address)
              $scope.serviceD.formatted_address = place.formatted_address;
            if (place.international_phone_number)
              $scope.serviceD.international_phone_number = place.international_phone_number;
            if (place.price_level){
              $scope.serviceD.price_level = "";
              for (var i = 0; i < place.price_level; ++i)
                $scope.serviceD.price_level += "$";
            }
            if (place.rating){
              $scope.serviceD.rating = place.rating;
              $scope.ratingStyle = {'width' : Math.max(0, (Math.min(5, place.rating)))*16+ "px"};
            }
            if (place.url)
              $scope.serviceD.url = place.url;
            if (place.website)
              $scope.serviceD.website = place.website;
            
            // time
            if (place.opening_hours){
                var date = new Date();
                var todayIdx = date.getDay();// 0 is sunday
                todayIdx--;
                if (todayIdx < 0)
                  todayIdx = 6; // 6 is Sunday
                var localtime = (date.getHours()*60+date.getMinutes()+(place.utc_offset+date.getTimezoneOffset()));
                if (localtime>24*60){
                  todayIdx++;
                  if (todayIdx > 6)
                      todayIdx = 0;
                }
                if (localtime < 0){
                  todayIdx--;
                  if (todayIdx < 0)
                      todayIdx = 6;
                }
                //console.log(todayIdx);
                $scope.serviceD.weekday_text = [];
                for (var i = todayIdx; i <= 6; ++ i){
                  var s =  place.opening_hours.weekday_text[i];
                  $scope.serviceD.weekday_text.push([s.substring(0,s.indexOf(":")), s.substring(s.indexOf(" "), s.length)]);
                }
                for (var i = 0; i < todayIdx; ++ i){
                  var s =  place.opening_hours.weekday_text[i];
                  $scope.serviceD.weekday_text.push([s.substring(0,s.indexOf(":")), s.substring(s.indexOf(" "), s.length)]);
                }
                if (place.opening_hours.open_now){
                  $scope.serviceD.opening = 'Open now:' + $scope.serviceD.weekday_text[todayIdx][1];
                  //$scope.serviceD.opening = 'Open now:' + time.substring(time.indexOf(' '),time.length);
                }
                else
                  $scope.serviceD.opening = 'Closed';
              }

            // photo
            var photos = place.photos;
            $scope.serviceD.photos = [];
            if (photos){
              for (var i = 0; i < photos.length; ++i)
                $scope.serviceD.photos.push(photos[i].getUrl({'maxWidth': photos[i].width, 'maxHeight' : photos[i].height})); 
            }
            $scope.serviceD.photos.length = Object.keys($scope.serviceD.photos).length;

            // review
            getReview();

            $scope.showOneDetail = true;
            $scope.showDetails = false;
            //console.log(place);
            $scope.$apply();
          }
      else {
        console.log("Google PlacesService Function Error");
        $scope.Error = true;
        $("#progress").css('display','none');
        $scope.$apply();
      }
    }

  };

  $scope.getDir = function(){
    function calculateAndDisplayRoute(directionsService, directionsDisplay) {
        var fromP;
        if (document.getElementById("inputFrom").value == $scope.inputFrom)
          fromP = {lat: lat, lng: lon};
        else 
          fromP = document.getElementById("inputFrom").value;
        directionsService.route({
          origin: fromP,
          destination: toP,
          travelMode: document.getElementById("inputMode").value,
          provideRouteAlternatives: true
        }, function(response, status) {
          if (status === 'OK') {
            directionsDisplay.setDirections(response);
          } else {
            window.alert('Directions request failed due to ' + status);
          }
        });
      }
    var directionsDisplay = new google.maps.DirectionsRenderer;
    var directionsService = new google.maps.DirectionsService;
    directionsDisplay.setMap(map);
    marker.setMap(null);
    document.getElementById('route-panel').innerHTML = "";
    directionsDisplay.setPanel(document.getElementById('route-panel'));
    calculateAndDisplayRoute(directionsService, directionsDisplay); 
  }

  $scope.getStreetView = function(){
    if (document.getElementById("ViewBnt").src == "http://cs-server.usc.edu:45678/hw/hw8/images/Pegman.png"){
      document.getElementById("ViewBnt").src = "http://cs-server.usc.edu:45678/hw/hw8/images/Map.png";
      document.getElementById("map").style.display = "none";
      document.getElementById("streetview").style.display = "block";
      panorama.setVisible(true);
    }
    else{
      document.getElementById("ViewBnt").src = "http://cs-server.usc.edu:45678/hw/hw8/images/Pegman.png";
      document.getElementById("map").style.display = "block";
      document.getElementById("streetview").style.display = "none";
      panorama.setVisible(false);
    }
  }

  $("#RewMenu .dropdown-item").click(function(){
    $("#RewBtn").html($(this).text());
    showReview();
  });
  $("#RatMenu .dropdown-item").click(function(){
    $("#RatBtn").html($(this).text());
    showReview();
  });

  var yelpReviews = [];
  var googleReviews = [];
  $scope.Reviews = [];
  function getReview(){
    var city = placeAll.formatted_address.split(',')[1];
    city = city.substring(1,city.length-1);
    var state = placeAll.formatted_address.split(',')[2];
    getRewData = {
        name: placeAll.name,
        address1: placeAll.formatted_address.split(',')[0],
        address2: city+", "+state.substring(1,city.length-1),
        city: city,
        state: state.split(' ')[1]
    }
    for (var i = placeAll.address_components.length-1; i >= 0 ; --i){
      if (placeAll.address_components[i].types[0] == "country"){
        getRewData.country = placeAll.address_components[i].short_name;
        break;
      }
    }
    //console.log(getRewData);
    $.ajax({
      type: "GET",
      url: "/yelp",
      data: getRewData,
      headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
      success: function(data){
        if (data == 'NoReview'){
          //no
          return false;
        }
        if (data == 'UnMatch'){
          return false;
        }

        // yelpReview
        var obj = JSON.parse(data);
        yelpReviews = [];
        for (var i = 0; i < obj.length; ++i){
          var review = [];
          review.img_url = obj[i].user.image_url;
          review.url = obj[i].url;
          review.name = obj[i].user.name;
          review.rating = obj[i].rating;
          review.ratingStyle = {'width' : Math.max(0, (Math.min(5, obj[i].rating)))*16+ "px"};
          review.time_text = obj[i].time_created;
          review.time = new Date(review.time_text).getTime()/1000;
          review.text = obj[i].text;
          yelpReviews.push(review);
        }
        //console.log(yelpReviews);

        // googleReviews
        obj = placeAll.reviews;
        googleReviews = [];
        for (var i = 0; i < obj.length; ++i){
          var review = [];
          review.img_url = obj[i].profile_photo_url;
          review.url = obj[i].author_url;
          review.name = obj[i].author_name;
          review.rating = obj[i].rating;
          review.ratingStyle = {'width' : Math.max(0, (Math.min(5, obj[i].rating)))*16+ "px"};
          review.time = obj[i].time;
          var time = new Date(review.time*1000);
          review.time_text = time.getFullYear()+"-"+time.getDay().toString().padStart(2,'0')+"-"+time.getMinutes().toString().padStart(2,'0')
                            +" "+time.getHours().toString().padStart(2,'0')+":"+time.getMinutes().toString().padStart(2,'0')+":"+time.getSeconds().toString().padStart(2,'0');
          review.text = obj[i].text;
          googleReviews.push(review);
        }
        showReview();
        //$scope.$apply();
      },
      error : function(){
        console.log("getReview Function Error");
        $scope.Error = true;
        $("#progress").css('display','none');
        $scope.$apply();
      }
    });
  }

  function showReview(){
    if (document.getElementById("RewBtn").innerHTML == "Google reviews")
      $scope.Reviews = googleReviews;
    else 
      $scope.Reviews = yelpReviews;

    if (document.getElementById("RatBtn").innerHTML== "Default Order")
      $scope.order = "";
    else if (document.getElementById("RatBtn").innerHTML =="Highest Rating")
      $scope.order = "-rating";
    else if (document.getElementById("RatBtn").innerHTML =="Lowest Rating")
      $scope.order = "rating";
    else if (document.getElementById("RatBtn").innerHTML =="Most Recent")
      $scope.order = "-time";
    else if (document.getElementById("RatBtn").innerHTML =="Least Recent")
      $scope.order = "time";
    $scope.$apply();
  }

  // Favorite
  $("#res").click(function(){
    if (!$scope.showOneDetail)
      $scope.Switch = false;
    $scope.showDetails = true;
    $scope.showOneDetail = false;
    $scope.isF = false;
    $scope.$apply();
  });
  $("#fav").click(function(){
    if (!$scope.showOneDetail)
      $scope.Switch = false;
    $scope.showDetails = true;
    $scope.showOneDetail = false;
    $scope.isF = true;
    $scope.$apply();
  });

  $scope.dupFav = function dupFav(place_id){
    if (place_id==undefined)
      return;
    for (var i=0; i<$scope.Favorites.length; ++i){
      if ($scope.Favorites[i].place_id == place_id)
        return true;
    }
    return false;
  }

  $scope.addFavorite = function(place_id){
    for (var i=0; i<$scope.Favorites.length; ++i)
      if ($scope.Favorites[i].place_id == place_id)
        return;
    var idx;
    for (var i=0; i<$scope.Details.results.length; ++i)
      if (place_id == $scope.Details.results[i].place_id)
        idx = i;
    if (idx!=undefined)
      $scope.Favorites.push($scope.Details.results[idx]);
    //console.log($scope.Favorites);
    //$scope.$apply();
  }

  $scope.removeFavorite = function(place_id){
    var idx;
    for (var i=0; i<$scope.Favorites.length; ++i)
      if ($scope.Favorites[i].place_id == place_id)
        idx = i;
    if (idx!=undefined)
      $scope.Favorites.splice(idx, 1);
    //$scope.$apply();
  }

  $scope.openTw = function(){
    var url = 'https://twitter.com/intent/tweet?text=Check out '+$scope.serviceD.name+' located at '+$scope.serviceD.formatted_address+'. Website: '+$scope.serviceD.website+' %23TravelAndEntertainmentSearch';
    //console.log(url);
    window.open(url,'Share a link on Twitter','location=no,status=no,scrollvars=no');
  }




});