import { describe, it, expect } from 'vitest';
import { calculateOnRoadPrice } from './price';

describe('calculateOnRoadPrice', () => {
  it('adds the configured price components', () => {
    expect(calculateOnRoadPrice(1245000, 87000, 42000, 18000)).toBe(1392000);
  });
  it('rejects negative components', () => {
    expect(() => calculateOnRoadPrice(100, -1, 0, 0)).toThrow();
  });
});
