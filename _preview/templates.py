from PIL import Image

# ---------- SWORD zones (from existing sea_bone_sword.png exact pixels) ----------
SWORD = {
    'blade': {(13,1),(14,1),(12,2),(13,2),(14,2),(11,3),(12,3),(13,3),(10,4),(11,4),(12,4),
              (9,5),(10,5),(11,5),(8,6),(9,6),(10,6),(4,7),(7,7),(8,7),(9,7),(4,8),(6,8),
              (7,8),(8,8),(4,9),(5,9),(6,9),(7,9),(6,10),(6,11),(7,11)},
    'edge_hi': {(13,0),(14,0),(15,0),(12,1),(11,2),(10,3),(9,4),(8,5)},
    'blade_low': {(15,1),(15,2),(14,3),(13,4),(12,5),(11,6),(10,7),(9,8),(8,9),(7,10),(5,8)},
    'guard': {(2,6),(3,6),(7,6),(2,7),(6,7),(3,8),(3,9),(4,10),(8,12),(0,13),(1,13),(0,14)},
    'highlight': {(3,7),(5,10),(1,14)},
    'handle': {(3,11),(2,12),(4,11),(2,13),(4,12),(3,13),(3,12)},
    'handle_dark': {(6,12),(7,12),(9,12),(8,13),(9,13),(2,14),(0,15),(1,15),(2,15),(5,11),(8,11)},
}

# ---------- KNIFE zones (hand-crafted dagger) ----------
KNIFE = {
    'blade': {(14,2),(13,2),(14,3),(13,3),(12,3),(13,4),(12,4),(11,4),(12,5),(11,5),(10,5),
              (11,6),(10,6),(9,6),(10,7),(9,7),(8,7),(9,8),(8,8),(7,8),(8,9),(7,9)},
    'edge_hi': {(15,1),(15,2),(14,1),(13,1),(13,3)},
    'blade_low': {(15,3),(14,4),(13,5),(12,6),(11,7),(10,8),(9,9)},
    'guard': {(2,10),(3,10),(4,10),(5,10),(6,10),(7,10),(8,10),(9,10),(10,10),
              (2,11),(3,11),(4,11),(5,11),(6,11),(7,11),(8,11),(9,11)},
    'highlight': {(5,12),(7,14)},
    'handle': {(3,12),(4,12),(5,12),(6,12),(7,12),(8,12),(3,13),(4,13),(5,13),(6,13),
               (7,13),(8,13),(3,14),(4,14),(5,14),(6,14),(7,14)},
    'handle_dark': {(2,12),(9,12),(2,13),(9,13),(2,14),(8,14),(3,15),(4,15),(5,15),(6,15)},
}


def dump_zones(zones):
    ch_map = {'blade': 'B', 'edge_hi': 'H', 'blade_low': 'L', 'guard': 'G',
              'highlight': '!', 'handle': 'h', 'handle_dark': 'D'}
    grid = [['.'] * 16 for _ in range(16)]
    for ch in ch_map:
        for x, y in zones[ch]:
            grid[y][x] = ch
    for row in grid:
        print(''.join(row))


def render(zones, pal, deco=None):
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    px = img.load()
    zone_color = {
        'blade': pal['blade'], 'edge_hi': pal['edge_hi'], 'blade_low': pal['blade_low'],
        'guard': pal['guard'], 'highlight': pal['highlight'], 'handle': pal['handle'],
        'handle_dark': pal['handle_dark'],
    }
    for zone, cells in zones.items():
        c = zone_color[zone]
        for x, y in cells:
            px[x, y] = c
    if deco:
        for (x, y), c in deco.items():
            if 0 <= x < 16 and 0 <= y < 16:
                px[x, y] = c
    return img


if __name__ == '__main__':
    print('== SWORD ==')
    dump_zones(SWORD)
    print('== KNIFE ==')
    dump_zones(KNIFE)
