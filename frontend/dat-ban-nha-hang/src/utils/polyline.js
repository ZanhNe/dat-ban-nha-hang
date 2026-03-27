/**
 * Giải mã Google Maps Encoded Polyline string thành Array of [longitude, latitude]
 * Lưu ý trả về [lng, lat] để dùng trực tiếp cho maplibre / turf
 */
export function decodePolyline(encoded) {
  if (!encoded) return [];
  let poly = [];
  let index = 0, len = encoded.length;
  let lat = 0, lng = 0;

  while (index < len) {
    let b, shift = 0, result = 0;
    do {
      b = encoded.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    let dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
    lat += dlat;

    shift = 0;
    result = 0;
    do {
      b = encoded.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    let dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
    lng += dlng;

    // Push [lng, lat]
    poly.push([lng / 1e5, lat / 1e5]);
  }
  return poly;
}
