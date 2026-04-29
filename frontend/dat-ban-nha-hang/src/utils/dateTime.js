export function getBookingStartTime(booking) {
  return booking?.bookingTime?.startTime || booking?.bookingTime || booking?.startTime || booking?.createdAt || null;
}

export function parseDateTime(value) {
  if (!value) return null;
  const parsed = value instanceof Date ? value : new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

export function formatDateTime(value, locale = 'vi-VN') {
  const parsed = parseDateTime(value);
  if (!parsed) {
    return {
      date: 'Không xác định',
      time: 'Không xác định'
    };
  }

  return {
    date: parsed.toLocaleDateString(locale, {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    }),
    time: parsed.toLocaleTimeString(locale, {
      hour: '2-digit',
      minute: '2-digit'
    })
  };
}
