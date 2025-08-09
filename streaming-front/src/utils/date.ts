export function formatDate(datetime: string | Date): string {
  if (!datetime) {
    return '날짜 정보 없음';
  }

  const date = new Date(datetime);

  if (isNaN(date.getTime())) {
    return '유효하지 않은 날짜';
  }

  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  const ss = String(date.getSeconds()).padStart(2, '0');

  return `${yyyy}.${mm}.${dd} ${hh}:${mi}:${ss}`;
}
