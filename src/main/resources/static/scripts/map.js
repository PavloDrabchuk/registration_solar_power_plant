
let map = new OpenLayers.Map("mapBlock", {
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
});

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

marker.events.register("click", marker, function(e){
    popup = new OpenLayers.Popup.FramedCloud("chicken",
        marker.lonlat,
        new OpenLayers.Size(200, 200),
        "example popup",
        null, true);

    map.addPopup(popup);
});

/*var marker1 = new khtml.maplib.overlay.Marker({
    position: new khtml.maplib.LatLng(-25.363882,131.044922),
    map: map,
    title:"static marker"
});*/



map.setCenter(lonLat, zoom);

