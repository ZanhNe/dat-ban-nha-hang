export function isApiResponse(body) {
  return (
    body &&
    typeof body === 'object' &&
    typeof body.status === 'number' &&
    typeof body.message === 'string' &&
    Object.prototype.hasOwnProperty.call(body, 'data')
  );
}

export function unwrapData(body) {
  return isApiResponse(body) ? body.data : body;
}

export function unwrapMeta(body) {
  return isApiResponse(body) ? body.meta : null;
}

export function unwrapStatus(body) {
  return isApiResponse(body) ? body.status : 200;
}

export function unwrapMessage(body) {
  return isApiResponse(body) ? body.message : '';
}

export function normalizeApiError(err) {
  const body = err?.response?.data;
  if (body && typeof body === 'object') {
    return {
      status: body.status ?? err?.response?.status ?? 0,
      message: body.message ?? err?.message ?? 'Lỗi không xác định',
      errors: body.errors ?? null
    };
  }
  return {
    status: err?.response?.status ?? 0,
    message: err?.message ?? 'Lỗi không xác định',
    errors: null
  };
}

function flattenErrors(errors) {
  if (!errors) return [];
  if (Array.isArray(errors)) {
    return errors.filter(Boolean).map(String);
  }
  if (typeof errors === 'object') {
    return Object.values(errors)
      .flatMap((value) => flattenErrors(value))
      .filter(Boolean);
  }
  return [String(errors)];
}

export function formatApiError(err, fallbackMessage = 'Có lỗi xảy ra, vui lòng thử lại.') {
  const normalized = normalizeApiError(err);
  const details = flattenErrors(normalized.errors);
  return {
    ...normalized,
    details,
    displayMessage: details[0] || normalized.message || fallbackMessage
  };
}

export function toLocalDateTimeParam(value, { endOfDay = false } = {}) {
  if (!value) return value;
  if (typeof value !== 'string') return value;
  if (value.includes('T')) return value;
  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return endOfDay ? `${value}T23:59:59` : `${value}T00:00:00`;
  }
  return value;
}

