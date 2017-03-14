<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
<link rel="shortcut icon" href='<c:url value="/web-resources/favicon.ico"/>' />
<meta charset="utf-8">
<title>Google Co-ordiantes</title>
<style>
html, body {
	height: 100%;
	margin: 0;
	padding: 0;
}

#map,.row {
	height: 100% !important;	
}
</style>
<link rel="stylesheet" href='<c:url value="/web-resources/css/bootstrap.min.css"/>'>
</head>
<body>
	<div class="row">
		<div class="col-xs-12 col-md-8" id="map"></div>
		<div class="col-xs-6 col-md-4">
			<form method="post" action="google-calc">
				<ul class="list-group">
				  <li class="list-group-item">
				  	<span>Distance Interval (KM)</span>
				  	<input class="form-control" type="number" placeholder="Distance Between Points" id="distance" name="distance" value="1"/>
				  </li>
				  <li class="list-group-item">
				  	<span>Country Code</span>
				  	<input class="form-control" type="text" placeholder="Country Code Ex: IN (India)" id="country" name="country" value="IN"/>
				  </li>
				  <li class="list-group-item">
				  	<span>Location</span>
				  	<input class="form-control" type="text" placeholder="Location Name" id="location" name="location" value="Delhi-NCR"/>
				  </li>
				  <li class="list-group-item">
				  	<span>Keywords</span>
				  	<input class="form-control" type="text" placeholder="Keywords...(tution, education, institute)" id="location" name="keywords" value=""/>
				  </li>
				  <li class="list-group-item">
					<span>North-West Coordinates</span>
				  	<input class="form-control" type="text" placeholder="North-West Points" id="nw" name="nw-cordinate" readonly="readonly"  />
				  </li>
				  <li class="list-group-item">
					<span>North-East Coordinates</span>
				  	<input class="form-control" type="text" placeholder="North-East Points" id="ne" name="ne-cordinate" readonly="readonly" />
				  </li>
				  <li class="list-group-item">
					<span>South-West Coordinates</span>
				  	<input class="form-control" type="text" placeholder="South-West Points" id="sw" name="sw-cordinate" readonly="readonly" />
				  </li>
				  <li class="list-group-item">
					<span>South-East Coordinates</span>
				  	<input class="form-control" type="text" placeholder="South-East Points" id="se" name="se-cordinate" readonly="readonly" />
				  </li>
				  <li class="list-group-item">
					<button class="btn btn-success">Calculate</button>
				  </li>
				</ul>
			</form>
		</div>
	</div>
	<script>
		// This example adds a user-editable rectangle to the map.
		// When the user changes the bounds of the rectangle,
		// an info window pops up displaying the new bounds.
		var rectangle;
		var map;
		var infoWindow;
		function initMap() {
			map = new google.maps.Map(document.getElementById('map'), {
				center : {
					lat : 28.631184,
					lng : 77.215916
				},
				zoom : 9
			});
			var bounds = {
				north : 28.642832,
				south : 28.607272,
				east : 77.240092,
				west : 77.181384
			};
			// Define the rectangle and set its editable property to true.
			rectangle = new google.maps.Rectangle({
				bounds : bounds,
				editable : true,
				draggable : true
			});
			rectangle.setMap(map);
			// Add an event listener on the rectangle.
			rectangle.addListener('bounds_changed', showNewRect);
			// Define an info window on the map.
			infoWindow = new google.maps.InfoWindow();
		}
		// Show the new coordinates for the rectangle in an info window.
		/** @this {google.maps.Rectangle} */
		function showNewRect(event) {
			var ne = rectangle.getBounds().getNorthEast();
			var sw = rectangle.getBounds().getSouthWest();
			var contentString = '<b>Rectangle moved.</b><br>'
					+ 'New north-east corner: ' + ne.lat() + ', ' + ne.lng()
					+ '<br>' + 'New south-west corner: ' + sw.lat() + ', '
					+ sw.lng();

			var cordinates = {};
			cordinates["north-east"] = ne.lat() + "," + ne.lng();
			cordinates["north-west"] = ne.lat() + "," + sw.lng();
			cordinates["south-east"] = sw.lat() + "," + ne.lng();
			cordinates["south-west"] = sw.lat() + "," + sw.lng();

			$("#ne").val(cordinates["north-east"]);
			$("#nw").val(cordinates["north-west"]);
			$("#se").val(cordinates["south-east"]);
			$("#sw").val(cordinates["south-west"]);
		}
	</script>
	
	<script src='<c:url value="/web-resources/js/jquery.js"/>' ></script>
	<script src='<c:url value="/web-resources/js/bootstrap.min.js"/>' ></script>
	<script async defer
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAw7COcSjls_IShYgBsg8K3bfHkj-PZLtQ&callback=initMap">
		
	</script>
	
	
	
	
	
</body>
</html>