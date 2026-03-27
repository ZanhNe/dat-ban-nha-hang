import React, { useEffect, useMemo, useRef } from 'react';
import Map, { Source, Layer, Marker } from 'react-map-gl/maplibre';
import 'maplibre-gl/dist/maplibre-gl.css';
import { useAtomValue } from 'jotai';
import * as turf from '@turf/turf';
import { MapPin } from 'lucide-react';

import {
  originAtom,
  radiusAtom,
  restaurantsAtom,
  focusModeAtom,
  selectedRestaurantAtom,
  restaurantDetailAtom
} from '../../store/mapStore';
import { decodePolyline } from '../../utils/polyline';

const MAPTILER_KEY = import.meta.env.VITE_MAPTILER;

export default function MapCanvas() {
  const mapRef = useRef();

  const origin = useAtomValue(originAtom);
  const radius = useAtomValue(radiusAtom);
  const restaurants = useAtomValue(restaurantsAtom);
  const focusMode = useAtomValue(focusModeAtom);
  const selectedRest = useAtomValue(selectedRestaurantAtom);
  const detail = useAtomValue(restaurantDetailAtom);

  const initialViewState = {
    longitude: 106.660172,
    latitude: 10.762622,
    zoom: 12
  };

  // 1. Tạo GeoJSON Circle từ origin và radius
  const radiusCircleGeoJSON = useMemo(() => {
    if (!origin || focusMode) return null; // Không vẽ vòng tròn trong Focus Mode
    return turf.circle(
      [origin.longitude, origin.latitude],
      radius,
      { steps: 64, units: 'kilometers' }
    );
  }, [origin, radius, focusMode]);

  // 2. Decode Polyline khi có detail
  const routeGeoJSON = useMemo(() => {
    if (!focusMode || !detail?.restaurantDirections?.routes?.[0]?.polyline?.points) return null;
    const polyParams = decodePolyline(detail.restaurantDirections.routes[0].polyline.points);
    if (!polyParams.length) return null;
    return turf.lineString(polyParams);
  }, [focusMode, detail]);

  // Lắng nghe sự thay đổi State để thao tác Camera (FlyTo, FitBounds)
  useEffect(() => {
    if (!mapRef.current) return;
    const map = mapRef.current.getMap();

    // Nếu Focus mode đang bật và có đường đi thì fitBounds
    if (focusMode && selectedRest && origin) {
      if (routeGeoJSON) {
        const bbox = turf.bbox(routeGeoJSON);
        map.fitBounds(bbox, { padding: 80, duration: 1000 });
      } else {
        // Dự phòng nếu API google direction chưa load xong -> fitbounds 2 điểm
        const restLng = selectedRest.restaurantLocation.longitude ?? selectedRest.restaurantLocation.lng;
        const restLat = selectedRest.restaurantLocation.latitude ?? selectedRest.restaurantLocation.lat;
        const line = turf.lineString([
          [origin.longitude, origin.latitude],
          [restLng, restLat]
        ]);
        const bbox = turf.bbox(line);
        map.fitBounds(bbox, { padding: 80, duration: 1000, maxZoom: 16 });
      }
    }
    // Nếu có Origin mới (và ko có focus mode) => flyTo tới tâm đó
    else if (origin && !focusMode) {
      // Zoom ratio dựa theo bán kính (gần đúng)
      const zoomLvl = radius <= 2 ? 14 : radius <= 5 ? 13 : radius <= 10 ? 12 : 11;
      map.flyTo({
        center: [origin.longitude, origin.latitude],
        zoom: zoomLvl,
        duration: 1000
      });
    }
  }, [origin, focusMode, selectedRest, routeGeoJSON, radius]);

  const mapStyleUrl = `https://api.maptiler.com/maps/streets-v2/style.json?key=${MAPTILER_KEY}`;

  // Custom fallback style if using goong
  let goongMapStyle = typeof MAPTILER_KEY === 'string' && MAPTILER_KEY.startsWith('http')
    ? MAPTILER_KEY
    : mapStyleUrl;

  // Nếu người dùng cung cấp link nguồn Tile (ví dụ URL có chứa sources/goong.json),
  // Cần đổi thành Style JSON URL để Maplibre có layer render.
  if (goongMapStyle.includes('sources/goong.json')) {
    goongMapStyle = goongMapStyle.replace('sources/goong.json', 'assets/goong_map_web.json');
  }

  return (
    <Map
      ref={mapRef}
      initialViewState={initialViewState}
      mapStyle={goongMapStyle}
      style={{ width: '100%', height: '100%' }}
    >
      {/* Vòng tròn bán kính */}
      {radiusCircleGeoJSON && (
        <Source type="geojson" data={radiusCircleGeoJSON}>
          <Layer
            id="radius-circle-fill"
            type="fill"
            paint={{
              'fill-color': '#3b82f6',
              'fill-opacity': 0.15
            }}
          />
          <Layer
            id="radius-circle-line"
            type="line"
            paint={{
              'line-color': '#3b82f6',
              'line-width': 2,
              'line-dasharray': [2, 2]
            }}
          />
        </Source>
      )}

      {/* Đường tuyến đường chỉ đường (Polyline) */}
      {routeGeoJSON && (
        <Source type="geojson" data={routeGeoJSON}>
          <Layer
            id="route-line"
            type="line"
            layout={{
              'line-join': 'round',
              'line-cap': 'round'
            }}
            paint={{
              'line-color': '#2563eb', // Blue
              'line-width': 4
            }}
          />
        </Source>
      )}

      {/* Marker Tâm User */}
      {origin && (
        <Marker longitude={origin.longitude} latitude={origin.latitude} anchor="bottom">
          <div className="relative flex items-center justify-center">
            <span className="absolute w-8 h-8 bg-blue-500 rounded-full animate-ping opacity-75"></span>
            <div className="relative bg-blue-600 text-white p-2 rounded-full shadow-lg border-2 border-white">
              <MapPin className="w-5 h-5" />
            </div>
          </div>
        </Marker>
      )}

      {/* Danh sách Markers Nhà Hàng */}
      {!focusMode && restaurants.map((res) => (
        <Marker
          key={res.restaurantId}
          longitude={res.restaurantLocation.longitude ?? res.restaurantLocation.lng}
          latitude={res.restaurantLocation.latitude ?? res.restaurantLocation.lat}
          anchor="bottom"
        >
          <div className="bg-red-500 text-white p-1.5 rounded-full shadow-md border-2 border-white cursor-pointer hover:bg-red-600 transition">
            <MapPin className="w-4 h-4" />
          </div>
        </Marker>
      ))}

      {/* Marker Nhà Hàng Đang Focus */}
      {focusMode && selectedRest && (
        <Marker
          longitude={selectedRest.restaurantLocation.longitude ?? selectedRest.restaurantLocation.lng}
          latitude={selectedRest.restaurantLocation.latitude ?? selectedRest.restaurantLocation.lat}
          anchor="bottom"
        >
          <div className="bg-orange-500 text-white p-2 rounded-full shadow-xl border-2 border-white transform scale-125 z-10">
            <MapPin className="w-5 h-5" />
          </div>
        </Marker>
      )}
    </Map>
  );
}
