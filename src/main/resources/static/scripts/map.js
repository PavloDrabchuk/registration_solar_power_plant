let iconFeature = new ol.Feature({
    geometry: new ol.geom.Point(ol.proj.fromLonLat([lon, lat])),
    name: solarPowerPlantName,
    population: 4000,
    rainfall: 500,
    desc:solarPowerPlantName,
    type:'click'
});

let iconStyle = new ol.style.Style({
    image: new ol.style.Icon({
        anchor: [0.5, 46],
        anchorXUnits: 'fraction',
        anchorYUnits: 'pixels',
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

map.on('click', function (evt) {
    const feature = map.forEachFeatureAtPixel(evt.pixel, function (ft) {
        return ft;
    });
    console.log("popup1: "+ evt.pixel+" feature: "+feature);
    if (feature && feature.get('type')==='click') {
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



