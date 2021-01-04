/*let map = new OpenLayers.Map("mapBlock", {
    controls: [
        new OpenLayers.Control.Navigation(),
        new OpenLayers.Control.PanZoomBar(),
        new OpenLayers.Control.LayerSwitcher(),
        new OpenLayers.Control.Attribution()],
    //maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
    //maxResolution: 156543.0399,
    numZoomLevels: 19,
    //units: 'm',
    projection: new OpenLayers.Projection("EPSG:900913"),
    displayProjection: new OpenLayers.Projection("EPSG:4326")
});*/

//import Map from ;

var iconFeature = new ol.Feature({
    geometry: new ol.geom.Point(ol.proj.fromLonLat([lon, lat])),
    name: 'Null Island',
    population: 4000,
    rainfall: 500,
});

var iconStyle = new ol.style.Style({
    image: new ol.style.Icon({
        anchor: [0.5, 46],
        anchorXUnits: 'fraction',
        anchorYUnits: 'pixels',
        src: 'https://openlayers.org/en/latest/examples/data/icon.png',
    }),
});

iconFeature.setStyle(iconStyle);

var vectorSource = new ol.source.Vector({
    features: [iconFeature],
});

var vectorLayer = new ol.layer.Vector({
    source: vectorSource,
});


let map = new ol.Map({
    target: document.getElementById('mapBlock'),

    layers: [
        new ol.layer.Tile({
            source: new ol.source.OSM()
        }), vectorLayer
    ],
    view: new ol.View({
        center: ol.proj.fromLonLat([lon, lat]),
        zoom: 12
    })
});

var element = document.getElementById('popup');
var content = document.getElementById('popup-content');

var popup = new ol.Overlay({
    element: element,
    positioning: 'bottom-center',
    stopEvent: false,
    offset: [0, -50],
});
map.addOverlay(popup);

// display popup on click
map.on('click', function (evt) {
    var feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
        return feature;
    });
    if (feature) {
        var coordinates = feature.getGeometry().getCoordinates();
        popup.setPosition(ol.proj.fromLonLat([lon, lat]));
        $(element).popover({
            placement: 'top',
            html: true,
            content: '<div class="popup">' +
                '<h3 class="popup-title">Title</h3>' +
                '<p class="popup-content">popup-content</p></div>',

        });
        $(element).popover('show');
    } else {
        $(element).popover('dispose');
    }
});


/*
// display popup on click
map.on('singleclick', function(evt) {
    var coordinate = evt.coordinate;

    var feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
        return feature;
    });
if(feature) {
    content.innerHTML = '<p>Test poput</p>';
    popup.setPosition(coordinate);
}
});*/

// change mouse cursor when over marker
map.on('pointermove', function (e) {
    console.log("e: "+e.type);
    if (e.dragging ) {

        $(element).popover('dispose');
        return;
    }
    var pixel = map.getEventPixel(e.originalEvent);
    var hit = map.hasFeatureAtPixel(pixel);
    map.getTarget().style.cursor = hit ? 'pointer' : '';
});

map.on('move', function (e) {
    console.log("-- e: "+e);
    /*if (e.dragging ) {
        console.log("e: "+e);
        $(element).popover('dispose');
        return;
    }
    var pixel = map.getEventPixel(e.originalEvent);
    var hit = map.hasFeatureAtPixel(pixel);
    map.getTarget().style.cursor = hit ? 'pointer' : '';*/
});

/*map.on("moveend", function(e){
    // event actions
    console.log("qwerty");
    var pixel = map.getEventPixel(e.originalEvent);
    var hit = map.hasFeatureAtPixel(pixel);
    map.getTarget().style.cursor = hit ? 'pointer' : '';
});*/

/*map.on("pointermove", function (evt) {
    var hit = this.forEachFeatureAtPixel(evt.pixel, function(feature, layer) {
        return true;
    });
    map.getTarget().style.cursor = hit ? 'pointer' : '';
});*/


/*

// Define the map layer
// Here we use a predefined layer that will be kept up to date with URL changes
layerMapnik = new OpenLayers.Layer.OSM.Mapnik("Mapnik");
map.addLayer(layerMapnik);
layerCycleMap = new OpenLayers.Layer.OSM.CycleMap("CycleMap");
map.addLayer(layerCycleMap);


layerMarkers = new OpenLayers.Layer.Markers("Markers");
map.addLayer(layerMarkers);


// Start position for the map (hardcoded here for simplicity,
// but maybe you want to get this from the URL params)


const zoom = 12;
let lonLat = new OpenLayers.LonLat(lon, lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());

// Add a Layer with Marker
let size = new OpenLayers.Size(21, 25);
let offset = new OpenLayers.Pixel(-(size.w / 2), -size.h);
let icon = new OpenLayers.Icon('https://www.openstreetmap.org/openlayers/img/marker.png', size, offset);

let marker= new OpenLayers.Marker(lonLat, icon);

layerMarkers.addMarker(marker);
let popup = new OpenLayers.Popup.FramedCloud("chicken",
    marker.lonlat,
    new OpenLayers.Size(200, 200),
    "example popup",
    null, true);

//click, mouseover, mouseout
marker.events.register("mouseover", marker, function(e){

/!*popupId=5;
    var popup = new OpenLayers.Popup.AnchoredBubble(popupId, marker.lonlat,
        new OpenLayers.Size(200,20),
        "Hello World ... "+popupId,
        null, true,closePopUp());*!/

    map.addPopup(popup);
});

marker.events.register("mouseout", marker, function(e){
    map.removePopup(popup);
});

/!*function closePopUp(){
    this.hide();
}*!/
/!*var marker1 = new khtml.maplib.overlay.Marker({
    position: new khtml.maplib.LatLng(-25.363882,131.044922),
    map: map,
    title:"static marker"
});*!/



map.setCenter(lonLat, zoom);
*/

