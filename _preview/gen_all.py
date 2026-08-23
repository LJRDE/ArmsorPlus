import sys
sys.path.insert(0, '_preview')
from templates import SWORD, KNIFE, render, dump_zones
from PIL import Image

OUT = r"ArmsorPlusResourcesPack/assets/armsorplus/textures/item"

# ---------- palettes ----------
# 6 swords, 5 knives share 6 themes (corrode_bone is sword-only)
THEMES = {}

THEMES['sea_bone'] = {
    'name': '海骨',
    'pal': {'blade': (232, 238, 232), 'edge_hi': (252, 255, 252), 'blade_low': (150, 172, 165),
            'guard': (26, 122, 140), 'highlight': (255, 255, 255),
            'handle': (158, 122, 78), 'handle_dark': (92, 72, 50)},
}

THEMES['spirit_bone'] = {
    'name': '灵骨',
    'pal': {'blade': (205, 222, 255), 'edge_hi': (240, 248, 255), 'blade_low': (118, 138, 190),
            'guard': (88, 112, 200), 'highlight': (255, 255, 255),
            'handle': (74, 92, 150), 'handle_dark': (40, 55, 95)},
}

THEMES['sea_spine'] = {
    'name': '海刺',
    'pal': {'blade': (60, 190, 190), 'edge_hi': (150, 242, 235), 'blade_low': (18, 112, 122),
            'guard': (235, 132, 118), 'highlight': (255, 255, 255),
            'handle': (96, 128, 112), 'handle_dark': (52, 72, 66)},
}

THEMES['corrode_bone'] = {
    'name': '蚀骨',
    'pal': {'blade': (168, 176, 150), 'edge_hi': (214, 219, 202), 'blade_low': (92, 104, 84),
            'guard': (92, 52, 122), 'highlight': (255, 240, 200),
            'handle': (74, 62, 62), 'handle_dark': (40, 34, 36)},
}

THEMES['spirit_spine'] = {
    'name': '灵刺',
    'pal': {'blade': (80, 240, 200), 'edge_hi': (205, 255, 242), 'blade_low': (18, 140, 122),
            'guard': (96, 160, 205), 'highlight': (255, 255, 255),
            'handle': (42, 112, 112), 'handle_dark': (20, 70, 70)},
}

THEMES['sea_cry'] = {
    'name': '海哭',
    'pal': {'blade': (70, 202, 255), 'edge_hi': (205, 250, 255), 'blade_low': (14, 112, 180),
            'guard': (42, 84, 180), 'highlight': (255, 255, 255),
            'handle': (52, 84, 140), 'handle_dark': (26, 46, 86)},
}

# ---------- decorations ----------

def glow(deco, zones, color, alpha, out=False):
    """translucent aura one pixel beyond the blade/edges."""
    core = zones['blade'] | zones['edge_hi'] | zones['blade_low']
    added = set()
    for x, y in core:
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                n = (x + dx, y + dy)
                if 0 <= n[0] < 16 and 0 <= n[1] < 16 and n not in core and n not in zones['guard'] and n not in zones['handle'] and n not in zones['handle_dark']:
                    added.add(n)
    for n in added:
        if out:
            deco[n] = (*color, alpha)
        elif n not in deco:
            deco[n] = (*color, alpha)


def spikes(deco, zones, color, tip_px):
    """spikes protruding beyond the blade on lower-right + upper-left edges."""
    core = zones['blade'] | zones['edge_hi'] | zones['blade_low']
    cand = [(x, y) for x in range(16) for y in range(16)
            if 0 <= x < 16 and 0 <= y < 16 and (x, y) not in core
            and any(abs(x - a) + abs(y - b) == 1 for a, b in core)]
    # prefer cells on the upper-left / lower-right flanks, matching the diagonal
    ranked = sorted(cand, key=lambda p: abs((p[0] - p[1]) - tip_px))
    for p in ranked[:8]:
        if p not in deco:
            deco[p] = color


def spots(deco, pos_color):
    for p, c in pos_color.items():
        if 0 <= p[0] < 16 and 0 <= p[1] < 16:
            deco[p] = c


# knife handle grip stripes (lighter pixels across the handle block)
def grip(zones, deco, light):
    handle = zones['handle']
    cols = sorted({x for x, y in handle})
    rows = sorted({y for x, y in handle})
    for i, y in enumerate(rows):
        if i % 2 == 0:
            continue
        for x in cols:
            if (x, y) in handle:
                deco[(x, y)] = light


GRIP = {
    'sea_bone': (182, 148, 104),
    'spirit_bone': (98, 116, 176),
    'sea_spine': (122, 152, 134),
    'spirit_spine': (66, 136, 136),
    'sea_cry': (76, 108, 166),
}

# per-theme decoration presets
DECO_SWORD = {
    'sea_bone': lambda z, d: (
        glow(d, z, (150, 235, 230), 70),
        spots(d, {(9, 5): (26, 140, 150), (7, 7): (26, 140, 150), (6, 9): (200, 255, 250), (8, 8): (150, 235, 230)}),
    ),
    'spirit_bone': lambda z, d: (
        glow(d, z, (160, 190, 255), 80),
        spots(d, {(12, 0): (215, 230, 255, 200), (11, 1): (205, 222, 255, 180), (7, 2): (225, 240, 255, 160),
                  (6, 4): (205, 222, 255, 140), (8, 12): (160, 190, 255, 90), (10, 9): (200, 220, 255, 90)}),
    ),
    'sea_spine': lambda z, d: (
        spots(d, {(9, 1): (235, 132, 118), (8, 3): (235, 132, 118), (7, 4): (250, 170, 150),
                  (10, 8): (235, 132, 118), (9, 9): (250, 170, 150), (8, 10): (235, 132, 118),
                  (11, 4): (250, 255, 255), (7, 8): (150, 242, 235)}),
    ),
    'corrode_bone': lambda z, d: (
        spots(d, {(9, 6): (116, 200, 86), (8, 7): (116, 200, 86), (6, 8): (150, 220, 100),
                  (12, 4): (146, 80, 185), (11, 5): (146, 80, 185), (10, 6): (120, 210, 90),
                  (8, 10): (116, 200, 86, 220), (7, 11): (116, 200, 86, 200)}),
    ),
    'spirit_spine': lambda z, d: (
        glow(d, z, (140, 255, 235), 90),
        spots(d, {(9, 1): (120, 255, 225), (8, 3): (120, 255, 225), (7, 4): (205, 255, 245),
                  (10, 8): (120, 255, 225), (9, 9): (205, 255, 245), (8, 10): (120, 255, 225),
                  (13, 2): (240, 255, 252), (12, 3): (240, 255, 252), (11, 4): (170, 255, 245)}),
    ),
    'sea_cry': lambda z, d: (
        glow(d, z, (130, 220, 255), 85),
        spots(d, {(13, 0): (255, 255, 255), (12, 1): (255, 255, 255), (14, 1): (255, 255, 255),
                  (13, 2): (255, 255, 255), (11, 1): (220, 245, 255), (8, 12): (130, 220, 255, 160),
                  (6, 10): (255, 255, 255), (9, 4): (150, 235, 255)}),
    ),
}

DECO_KNIFE = {
    'sea_bone': lambda z, d: (
        glow(d, z, (150, 235, 230), 60),
        spots(d, {(9, 6): (26, 140, 150), (8, 7): (26, 140, 150), (7, 9): (200, 255, 250)}),
        grip(z, d, GRIP['sea_bone']),
    ),
    'spirit_bone': lambda z, d: (
        glow(d, z, (160, 190, 255), 70),
        spots(d, {(12, 2): (215, 230, 255, 190), (10, 4): (205, 222, 255, 160), (8, 6): (225, 240, 255, 140), (6, 9): (205, 222, 255, 130)}),
        grip(z, d, GRIP['spirit_bone']),
    ),
    'sea_spine': lambda z, d: (
        spots(d, {(9, 2): (235, 132, 118), (8, 4): (235, 132, 118), (7, 5): (250, 170, 150),
                  (10, 8): (235, 132, 118), (9, 9): (250, 170, 150), (8, 10): (235, 132, 118)}),
        grip(z, d, GRIP['sea_spine']),
    ),
    'spirit_spine': lambda z, d: (
        glow(d, z, (140, 255, 235), 80),
        spots(d, {(9, 2): (120, 255, 225), (8, 4): (120, 255, 225), (7, 5): (205, 255, 245),
                  (10, 8): (120, 255, 225), (9, 9): (205, 255, 245), (8, 10): (120, 255, 225),
                  (13, 3): (240, 255, 252), (12, 4): (240, 255, 252)}),
        grip(z, d, GRIP['spirit_spine']),
    ),
    'sea_cry': lambda z, d: (
        glow(d, z, (130, 220, 255), 75),
        spots(d, {(14, 1): (255, 255, 255), (13, 2): (255, 255, 255), (14, 2): (255, 255, 255),
                  (12, 3): (255, 255, 255), (11, 3): (220, 245, 255), (7, 10): (255, 255, 255), (5, 12): (150, 235, 255)}),
        grip(z, d, GRIP['sea_cry']),
    ),
}

# ---------- build ----------
weapons = [
    ('sea_bone_sword', 'sea_bone', 'sword'),
    ('sea_bone_knife', 'sea_bone', 'knife'),
    ('spirit_bone_sword', 'spirit_bone', 'sword'),
    ('spirit_bone_knife', 'spirit_bone', 'knife'),
    ('sea_spine_sword', 'sea_spine', 'sword'),
    ('sea_spine_knife', 'sea_spine', 'knife'),
    ('corrode_bone_sword', 'corrode_bone', 'sword'),
    ('spirit_spine_sword', 'spirit_spine', 'sword'),
    ('spirit_spine_knife', 'spirit_spine', 'knife'),
    ('sea_cry_sword', 'sea_cry', 'sword'),
    ('sea_cry_knife', 'sea_cry', 'knife'),
]

import os
os.makedirs(OUT, exist_ok=True)
for fname, theme, kind in weapons:
    zones = SWORD if kind == 'sword' else KNIFE
    pal = THEMES[theme]['pal']
    deco = {}
    fn = (DECO_SWORD if kind == 'sword' else DECO_KNIFE)[theme]
    fn(zones, deco)
    img = render(zones, pal, deco)
    path = os.path.join(OUT, fname + '.png')
    img.save(path)
    print(f'{fname}: {img.getpixel((14,1))} saved')
print('done')
