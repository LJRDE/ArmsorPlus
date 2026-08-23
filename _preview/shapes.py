import math
from PIL import Image

# ---------- geometry ----------

def line_cells(x0, y0, x1, y1, w0, w1):
    """Tapered diagonal band: cells within radius w0 (at start) .. w1 (at end)."""
    pts = set()
    steps = max(abs(x1 - x0), abs(y1 - y0)) * 4 + 1
    for i in range(steps + 1):
        t = i / steps
        x = x0 + (x1 - x0) * t
        y = y0 + (y1 - y0) * t
        cx, cy = int(round(x)), int(round(y))
        w = w0 + (w1 - w0) * t
        r = max(0, int(round(w)))
        for dx in range(-r, r + 1):
            for dy in range(-r, r + 1):
                if abs(dx) + abs(dy) <= r + 1:
                    pts.add((cx + dx, cy + dy))
    return pts


def shifted(cells, dx, dy):
    return {(x + dx, y + dy) for x, y in cells}


def within16(pts):
    return {(x, y) for x, y in pts if 0 <= x < 16 and 0 <= y < 16}


# ---------- shape templates ----------

def sword_shape():
    """classic diagonal sword: blade up-right, guard, handle, pommel"""
    blade = line_cells(11, 2, 4, 9, 1.6, 1.0)   # blade body core
    # sharpen tip by adding explicit tip pixels top-right
    blade |= {(13, 1), (14, 1), (13, 2), (14, 2), (12, 2)}
    blade = within16(blade)
    # upper-left edge highlight (offset perpendicular-ish to -x,-y)
    edge_hi = shifted(blade, -1, -1) - blade
    edge_lo = shifted(blade, 1, 1) - blade       # lower-right edge shadow
    # guard bar (horizontal), 2 rows
    guard = within16({(x, y) for x in range(1, 11) for y in (10, 11)})
    # handle below-left
    handle = within16({(x, y) for x in range(2, 7) for y in range(12, 15)})
    # pommel
    pommel = within16({(x, y) for x in range(2, 5) for y in (15,)})
    return {
        'blade': blade, 'edge_hi': edge_hi, 'edge_lo': edge_lo,
        'guard': guard, 'handle': handle, 'pommel': pommel,
    }


def knife_shape():
    """short dagger: shorter blade, bigger handle"""
    blade = line_cells(12, 2, 7, 9, 1.4, 1.0)
    blade |= {(14, 1), (14, 2), (13, 2), (12, 1)}
    blade = within16(blade)
    edge_hi = shifted(blade, -1, -1) - blade
    edge_lo = shifted(blade, 1, 1) - blade
    guard = within16({(x, y) for x in range(2, 10) for y in (10, 11)})
    handle = within16({(x, y) for x in range(3, 8) for y in range(12, 15)})
    pommel = within16({(x, y) for x in range(3, 6) for y in (15,)})
    return {
        'blade': blade, 'edge_hi': edge_hi, 'edge_lo': edge_lo,
        'guard': guard, 'handle': handle, 'pommel': pommel,
    }


def dump(shape):
    zones = {
        'B': shape['blade'], 'H': shape['edge_hi'], 'L': shape['edge_lo'],
        'G': shape['guard'], 'h': shape['handle'], 'P': shape['pommel'],
    }
    grid = [['.'] * 16 for _ in range(16)]
    for ch, cells in zones.items():
        for x, y in cells:
            grid[y][x] = ch
    for row in grid:
        print(''.join(row))


if __name__ == '__main__':
    print('== SWORD ==')
    dump(sword_shape())
    print('== KNIFE ==')
    dump(knife_shape())
