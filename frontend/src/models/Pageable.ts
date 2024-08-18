export interface Pageable<T> {
  totalElements: number;
  totalPages: number;
  size: number;
  content: T[];
  number: number;
}

export function emptyPageable<T>(): Pageable<T> {
  return {
    totalElements: 0,
    totalPages: 0,
    size: 0,
    content: [],
    number: 0,
  };
}
