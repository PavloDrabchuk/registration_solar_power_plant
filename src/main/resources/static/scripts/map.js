/*import('/modules/ol/Map')
    .then(obj => Map)
        .catch(err => 'Error')*/

// import * as ol from "../modules/ol";

//import('../modules/ol/Map.js');

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

//import Map from '/modules/ol/Map';

let iconFeature = new ol.Feature({
    geometry: new ol.geom.Point(ol.proj.fromLonLat([lon, lat])),
    name: solarPowerPlantName,
    population: 4000,
    rainfall: 500,
    desc:solarPowerPlantName,
    type:'click'
});

/*let iconFeature2 = new ol.Feature({
    geometry: new ol.geom.Point(ol.proj.fromLonLat([lon+0.05, lat+0.05])),
    name: 'Null Island2',
    population: 4000,
    rainfall: 500,
    desc:'second',
    type:'click'
});*/

let iconStyle = new ol.style.Style({
    image: new ol.style.Icon({
        anchor: [0.5, 46],
        anchorXUnits: 'fraction',
        anchorYUnits: 'pixels',
        //src: 'https://openlayers.org/en/latest/examples/data/icon.png',
        src: '/images/icons/icon.png',
    }),
});

iconFeature.setStyle(iconStyle);
//iconFeature2.setStyle(iconStyle);

let vectorSource = new ol.source.Vector({
    features: [iconFeature/*, iconFeature2*/],
});

let vectorLayer = new ol.layer.Vector({
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
        zoom: 16,
        minZoom: 2,
        maxZoom: 19
    })
});

const element = document.getElementById('popup');
const content = document.getElementById('popup-content');


const popup = new ol.Overlay({
    element: element,
    positioning: 'bottom-center',
    stopEvent: false,
    offset: [0, -50],
});
map.addOverlay(popup);

// display popup on click
map.on('click', function (evt) {
    const feature = map.forEachFeatureAtPixel(evt.pixel, function (ft) {
        return ft;
    });
    console.log("popup1: "+ evt.pixel+" feature: "+feature);
    if (feature && feature.get('type')==='click') {
        //const coordinates = feature.getGeometry().getCoordinates();
        popup.setPosition(ol.proj.fromLonLat([lon, lat]));
        $(element).popover({
            placement: 'top',
            html: true,
            content: '<div class="popup">' +
                '<h3 class="popup-title">'+feature.get('desc')+'</h3>' +
                '<p class="popup-content">Короткі дані</p></div>',
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
    // console.log("e: "+e.type);
    if (e.dragging ) {
        $(element).popover('dispose');
        return;
    }
    const pixel = map.getEventPixel(e.originalEvent);
    const hit = map.hasFeatureAtPixel(pixel);
    map.getTarget().style.cursor = hit ? 'pointer' : '';
});

map.on('movestart', function (e) {
    $(element).popover('dispose');
});

//**********************************************//

/*const element2 = document.getElementById('popup2');
const content2 = document.getElementById('popup-content2');


const popup2 = new ol.Overlay({
    element: element2,
    positioning: 'bottom-center',
    stopEvent: false,
    offset: [0, -50],
});
map.addOverlay(popup2);

// display popup on click
map.on('click', function (evt) {
    const feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
        return feature;
    });
    console.log("popup2");
    if (feature) {
        //const coordinates = feature.getGeometry().getCoordinates();
        popup2.setPosition(ol.proj.fromLonLat([lon+0.05, lat+0.05]));
        $(element2).popover({
            placement: 'top',
            html: true,
            content: '<div class="popup">' +
                '<h3 class="popup-title">Title2</h3>' +
                '<p class="popup-content">popup-2</p></div>',

        });
        $(element2).popover('show');
    } else {
        $(element2).popover('dispose');
    }
});


/!*
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
});*!/

// change mouse cursor when over marker
map.on('pointermove', function (e) {
    // console.log("e: "+e.type);
    if (e.dragging ) {
        $(element).popover('dispose');
        return;
    }
    const pixel = map.getEventPixel(e.originalEvent);
    const hit = map.hasFeatureAtPixel(pixel);
    map.getTarget().style.cursor = hit ? 'pointer' : '';
});

map.on('movestart', function (e) {
    $(element2).popover('dispose');
});*/

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

