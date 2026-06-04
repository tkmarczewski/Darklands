import os
import math

filenames = [
    "ic_equipment_sword","ic_equipment_axe","ic_equipment_spear","ic_equipment_shield",
    "ic_equipment_helmet","ic_equipment_armor","ic_equipment_pouch","ic_equipment_book",
    "ic_equipment_potion_red","ic_equipment_potion_blue","ic_equipment_potion_green","ic_equipment_scroll",
    "ic_stats_str","ic_stats_dex","ic_stats_will","ic_stats_know",
    "ic_stats_cha","ic_stats_luck","ic_stats_perception","ic_stats_endurance",
    "ic_status_poison","ic_status_bleed","ic_status_fear","ic_status_bless",
    "ic_status_fatigue","ic_status_freeze","ic_status_fire","ic_status_intox",
    "ic_status_curse","ic_status_protect","ic_status_regen","ic_status_silence",
    "ic_alchemy_hp_plus","ic_alchemy_mp_plus","ic_alchemy_str","ic_alchemy_dex",
    "ic_alchemy_res","ic_alchemy_oil","ic_alchemy_strong_poison","ic_alchemy_antidote",
    "ic_rune_def","ic_rune_atk","ic_rune_alch","ic_sigil_fire",
    "ic_sigil_ice","ic_sigil_poison","ic_sigil_light","ic_sigil_shadow",
    "ic_combat_attack","ic_combat_defend","ic_combat_dodge","ic_combat_counter",
    "ic_combat_special","ic_combat_magic","ic_combat_shoot","ic_combat_break",
    "ic_damage_slash","ic_damage_pierce","ic_damage_blunt","ic_damage_fire",
    "ic_system_map","ic_system_journal","ic_system_quests","ic_system_settings"
]

class SimpleImage:
    def __init__(self, size, bg_color):
        self.width, self.height = size
        self.pixels = [list(bg_color) for _ in range(self.width * self.height)]

    def set_pixel(self, x, y, color):
        if color is None: return
        ix, iy = int(x), int(y)
        if 0 <= ix < self.width and 0 <= iy < self.height:
            self.pixels[iy * self.width + ix] = list(color[:3])

    def save_ppm(self, path):
        with open(path, 'wb') as f:
            f.write(f"P6\n{self.width} {self.height}\n255\n".encode())
            for p in self.pixels:
                f.write(bytes(p))

class SimpleDraw:
    def __init__(self, img):
        self.img = img

    def rectangle(self, bounds, fill):
        if fill is None: return
        x0, y0 = bounds[0]
        x1, y1 = bounds[1]
        for y in range(int(y0), int(y1) + 1):
            for x in range(int(x0), int(x1) + 1):
                self.img.set_pixel(x, y, fill)

    def ellipse(self, bounds, fill):
        if fill is None: return
        x0, y0 = bounds[0]
        x1, y1 = bounds[1]
        cx, cy = (x0 + x1) / 2.0, (y0 + y1) / 2.0
        rx, ry = abs(x1 - x0) / 2.0, abs(y1 - y0) / 2.0
        if rx == 0 or ry == 0: return
        for y in range(int(y0), int(y1) + 1):
            for x in range(int(x0), int(x1) + 1):
                if ((x - cx)**2 / rx**2) + ((y - cy)**2 / ry**2) <= 1.0:
                    self.img.set_pixel(x, y, fill)

    def line(self, points, fill, width=1):
        if fill is None: return
        x0, y0 = points[0]
        x1, y1 = points[1]
        dx = abs(x1 - x0)
        dy = abs(y1 - y0)
        steps = int(max(dx, dy))
        if steps == 0:
            self.img.set_pixel(x0, y0, fill)
            return
        for i in range(steps + 1):
            x = x0 + i * (x1 - x0) / steps
            y = y0 + i * (y1 - y0) / steps
            if width <= 1:
                self.img.set_pixel(x, y, fill)
            else:
                for ox in range(width):
                    for oy in range(width):
                        self.img.set_pixel(x + ox - width//2, y + oy - width//2, fill)

    def polygon(self, pts, fill):
        if fill is None: return
        min_x = int(min(p[0] for p in pts))
        max_x = int(max(p[0] for p in pts))
        min_y = int(min(p[1] for p in pts))
        max_y = int(max(p[1] for p in pts))
        for y in range(min_y, max_y + 1):
            for x in range(min_x, max_x + 1):
                inside = False
                j = len(pts) - 1
                for i in range(len(pts)):
                    if ((pts[i][1] > y) != (pts[j][1] > y)) and \
                       (x < (pts[j][0] - pts[i][0]) * (y - pts[i][1]) / (pts[j][1] - pts[i][1] + 1e-9) + pts[i][0]):
                        inside = not inside
                    j = i
                if inside:
                    self.img.set_pixel(x, y, fill)

def s(v): return v

# --- DRAWING FUNCTIONS ---
def draw_sword(draw, c, color):
    col = color or (180, 180, 190)
    draw.polygon([(s(c[0]-3),s(c[1]+20)),(s(c[0]+3),s(c[1]+20)),(s(c[0]+1),s(c[1]-20)),(s(c[0]-1),s(c[1]-20))], fill=col)
    draw.rectangle([(s(c[0]-10),s(c[1]+18)),(s(c[0]+10),s(c[1]+22))], fill=(120,90,50))
    draw.rectangle([(s(c[0]-2),s(c[1]+22)),(s(c[0]+2),s(c[1]+30))], fill=(80,60,30))

def draw_axe(draw, c, color):
    col = color or (180, 180, 190)
    draw.polygon([(s(c[0]-12),s(c[1]-10)),(s(c[0]+4),s(c[1]-18)),(s(c[0]+4),s(c[1]+2)),(s(c[0]-12),s(c[1]-4))], fill=col)
    draw.rectangle([(s(c[0]+2),s(c[1]-20)),(s(c[0]+6),s(c[1]+22))], fill=(100,70,40))

def draw_spear(draw, c, color):
    col = color or (180, 180, 190)
    draw.rectangle([(s(c[0]-2),s(c[1]+30)),(s(c[0]+2),s(c[1]-10))], fill=(100,70,40))
    draw.polygon([(s(c[0]-5),s(c[1]-10)),(s(c[0]+5),s(c[1]-10)),(s(c[0]),s(c[1]-25))], fill=col)

def draw_shield(draw, c, color):
    col = color or (100, 70, 40)
    draw.ellipse([(s(c[0]-16),s(c[1]-18)),(s(c[0]+16),s(c[1]+18))], fill=col)
    draw.polygon([(s(c[0]-8),s(c[1]-12)),(s(c[0]+8),s(c[1]-12)),(s(c[0]),s(c[1]+14))], fill=(50,60,70))
    draw.line([(s(c[0]),s(c[1]-18)),(s(c[0]),s(c[1]+18))], fill=(80,90,100), width=s(2))

def draw_helmet(draw, c, color):
    col = color or (160, 160, 170)
    draw.ellipse([(s(c[0]-16),s(c[1]-18)),(s(c[0]+16),s(c[1]+4))], fill=col)
    draw.rectangle([(s(c[0]-16),s(c[1])),(s(c[0]+16),s(c[1]+8))], fill=col)
    draw.rectangle([(s(c[0]-4),s(c[1]+2)),(s(c[0]+4),s(c[1]+10))], fill=(40,40,50))

def draw_armor(draw, c, color):
    col = color or (150, 150, 160)
    draw.rectangle([(s(c[0]-14),s(c[1]-16)),(s(c[0]+14),s(c[1]+18))], fill=col)
    for x in [-10,-4,2,8]:
        draw.ellipse([(s(c[0]+x-2),s(c[1]-8)),(s(c[0]+x+2),s(c[1]-2))], fill=(80,90,100))

def draw_pouch(draw, c, color):
    col = color or (120, 90, 50)
    draw.ellipse([(s(c[0]-14),s(c[1]-6)),(s(c[0]+14),s(c[1]+16))], fill=col)
    draw.rectangle([(s(c[0]-6),s(c[1]-16)),(s(c[0]+6),s(c[1]-6))], fill=(100,75,40))
    for x in [-4,0,4]:
        draw.ellipse([(s(c[0]+x-2),s(c[1]+2)),(s(c[0]+x+2),s(c[1]+6))], fill=(220,180,40))

def draw_book(draw, c, color):
    col = color or (140, 60, 40)
    draw.rectangle([(s(c[0]-14),s(c[1]-18)),(s(c[0]+14),s(c[1]+18))], fill=col)
    draw.rectangle([(s(c[0]-14),s(c[1]-18)),(s(c[0]-10),s(c[1]+18))], fill=(60,40,20))
    for y in [-8,-3,2]:
        draw.line([(s(c[0]-6),s(c[1]+y)),(s(c[0]+10),s(c[1]+y))], fill=(200,170,100), width=s(1))
    draw.rectangle([(s(c[0]-2),s(c[1]-18)),(s(c[0]+2),s(c[1]+18))], fill=(100,60,20))

def draw_potion(draw, c, color):
    col = color or (200, 200, 200)
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=col)
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(140,140,140))
    draw.ellipse([(s(c[0]-3),s(c[1]+6)),(s(c[0]+3),s(c[1]+12))], fill=(min(col[0]+80,255),min(col[1]+80,255),min(col[2]+80,255)))

def draw_scroll(draw, c, color):
    col = color or (220, 200, 160)
    draw.rectangle([(s(c[0]-12),s(c[1]-14)),(s(c[0]+12),s(c[1]+14))], fill=col)
    draw.ellipse([(s(c[0]-14),s(c[1]-16)),(s(c[0]+14),s(c[1]-8))], fill=(180,160,100))
    draw.ellipse([(s(c[0]-14),s(c[1]+8)),(s(c[0]+14),s(c[1]+16))], fill=(180,160,100))
    draw.ellipse([(s(c[0]-6),s(c[1]+4)),(s(c[0]+6),s(c[1]+10))], fill=(120,80,40))

def draw_stats_str(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(140,20,20))
    pts = [(s(c[0]-8),s(c[1]-10)),(s(c[0]-5),s(c[1]-12)),(s(c[0]-5),s(c[1]-2)),
           (s(c[0]-8),s(c[1]+4)),(s(c[0]-6),s(c[1]+6)),(s(c[0]+4),s(c[1]+8)),
           (s(c[0]+6),s(c[1]+6)),(s(c[0]+4),s(c[1]-8)),(s(c[0]+8),s(c[1]-2)),
           (s(c[0]+6),s(c[1]+0)),(s(c[0]+10),s(c[1]+10)),(s(c[0]-8),s(c[1]+10))]
    draw.polygon(pts, fill=(240,200,160))

def draw_stats_dex(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,120,40))
    draw.polygon([(s(c[0]),s(c[1]-16)),(s(c[0]+2),s(c[1]-16)),(s(c[0]+8),s(c[1]+8)),
                  (s(c[0]+5),s(c[1]+8)),(s(c[0]+5),s(c[1]+16)),(s(c[0]-5),s(c[1]+16)),
                  (s(c[0]-5),s(c[1]+8)),(s(c[0]-8),s(c[1]+8))], fill=(180,230,120))

def draw_stats_will(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,20,100))
    draw.polygon([(s(c[0]),s(c[1]-16)),(s(c[0]+4),s(c[1]-4)),(s(c[0]+8),s(c[1]+12)),
                  (s(c[0]),s(c[1]+6)),(s(c[0]-8),s(c[1]+12)),(s(c[0]-4),s(c[1]-4))], fill=(240,220,80))
    draw.ellipse([(s(c[0]-2),s(c[1]-2)),(s(c[0]+2),s(c[1]+2))], fill=(255,200,50))

def draw_stats_know(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,60,140))
    draw.rectangle([(s(c[0]-10),s(c[1]-12)),(s(c[0]+10),s(c[1]+12))], fill=(200,170,100))
    draw.rectangle([(s(c[0]-10),s(c[1]-12)),(s(c[0]-8),s(c[1]+12))], fill=(60,40,20))
    for y in [-5,-1,3]:
        draw.line([(s(c[0]-4),s(c[1]+y)),(s(c[0]+6),s(c[1]+y))], fill=(80,50,20), width=s(1))

def draw_stats_cha(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(100,80,0))
    draw.ellipse([(s(c[0]-10),s(c[1]-12)),(s(c[0]+10),s(c[1]+4))], fill=(220,180,100))
    draw.ellipse([(s(c[0]-5),s(c[1]+0)),(s(c[0]-1),s(c[1]+4))], fill=(80,40,20))
    draw.ellipse([(s(c[0]+1),s(c[1]+0)),(s(c[0]+5),s(c[1]+4))], fill=(80,40,20))

def draw_stats_luck(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(40,40,80))
    draw.ellipse([(s(c[0]-8),s(c[1]-8)),(s(c[0]+8),s(c[1]+8))], fill=(220,180,40))
    draw.ellipse([(s(c[0]-4),s(c[1]-4)),(s(c[0]+4),s(c[1]+4))], fill=(180,140,20))
    draw.polygon([(s(c[0]),s(c[1]-2)),(s(c[0]+2),s(c[1]+1)),(s(c[0]+1),s(c[1]+3)),
                  (s(c[0]),s(c[1]+1)),(s(c[0]-1),s(c[1]+3)),(s(c[0]-2),s(c[1]+1))], fill=(140,100,0))

def draw_stats_perception(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(40,60,100))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(140,180,240))
    draw.ellipse([(s(c[0]-4),s(c[1]-4)),(s(c[0]+4),s(c[1]+4))], fill=(100,20,20))
    for i in range(4):
        a = i * (math.pi / 2.0)
        x1 = s(c[0] + int(math.cos(a) * 12))
        y1 = s(c[1] + int(math.sin(a) * 12))
        x2 = s(c[0] + int(math.cos(a) * 15))
        y2 = s(c[1] + int(math.sin(a) * 15))
        draw.line([(x1,y1),(x2,y2)], fill=(60,60,80), width=s(2))

def draw_stats_endurance(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,60,40))
    draw.ellipse([(s(c[0]-10),s(c[1]-6)),(s(c[0]+10),s(c[1]+10))], fill=(200,80,80))
    draw.rectangle([(s(c[0]-12),s(c[1]-4)),(s(c[0]+12),s(c[1]+2))], fill=(160,160,180))

def draw_status_poison(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,80,20))
    draw.ellipse([(s(c[0]-7),s(c[1]-10)),(s(c[0]-2),s(c[1]-4))], fill=(200,220,180))
    draw.ellipse([(s(c[0]+2),s(c[1]-10)),(s(c[0]+7),s(c[1]-4))], fill=(200,220,180))
    draw.polygon([(s(c[0]-5),s(c[1]+2)),(s(c[0]+5),s(c[1]+2)),(s(c[0]),s(c[1]+12))], fill=(180,200,160))

def draw_status_bleed(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,10,10))
    for i in range(3):
        y = s(c[1]-8+i*10)
        draw.polygon([(s(c[0]-3+i),y),(s(c[0]+1-i),y),(s(c[0]-1),s(c[1]+6))], fill=(200,60,60))

def draw_status_fear(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(40,50,80))
    draw.polygon([(s(c[0]-6),s(c[1]-2)),(s(c[0]+6),s(c[1]-2)),(s(c[0]),s(c[1]+10))], fill=(220,210,180))
    draw.ellipse([(s(c[0]-4),s(c[1]-8)),(s(c[0]+4),s(c[1]-2))], fill=(180,160,120))
    draw.polygon([(s(c[0]-3),s(c[1]+12)),(s(c[0]),s(c[1]+14)),(s(c[0]+3),s(c[1]+12))], fill=(100,100,140))

def draw_status_bless(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,20,80))
    draw.line([(s(c[0]),s(c[1]-12)),(s(c[0]),s(c[1]+12))], fill=(255,220,40), width=s(3))
    draw.line([(s(c[0]-10),s(c[1]-2)),(s(c[0]+10),s(c[1]-2))], fill=(255,220,40), width=s(3))
    for i in [-8,0,8]:
        draw.line([(s(c[0]),s(c[1]-10)),(s(c[0])+s(3),s(c[1]-14))], fill=(255,200,60), width=s(1))

def draw_status_fatigue(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,70,50))
    draw.ellipse([(s(c[0]-8),s(c[1]-8)),(s(c[0]+8),s(c[1]+0))], fill=(160,140,100))
    draw.polygon([(s(c[0]-6),s(c[1]+0)),(s(c[0]+6),s(c[1]+0)),(s(c[0]),s(c[1]+10))], fill=(140,120,80))

def draw_status_freeze(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,40,100))
    draw.line([(s(c[0]),s(c[1]-14)),(s(c[0]),s(c[1]+14))], fill=(200,230,255), width=s(2))
    draw.line([(s(c[0]-12),s(c[1])),(s(c[0]+12),s(c[1]))], fill=(200,230,255), width=s(2))
    draw.line([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(200,230,255), width=s(2))
    draw.line([(s(c[0]-10),s(c[1]+10)),(s(c[0]+10),s(c[1]-10))], fill=(200,230,255), width=s(2))

def draw_status_fire(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(100,20,0))
    pts = [(s(c[0]),s(c[1]-14)),(s(c[0])+10,s(c[1]+4)),(s(c[0])-6,s(c[1]+6)),
           (s(c[0])+8,s(c[1]+10)),(s(c[0])-10,s(c[1]+12)),(s(c[0]),s(c[1]+8))]
    draw.polygon(pts, fill=(255,140,0))

def draw_status_intox(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,40,80))
    for i in range(3):
        y = s(c[1]-10+i*10)
        draw.line([(s(c[0]-8+i*4),y),(s(c[0]+8-i*4),y)], fill=(180,140,200), width=s(2))

def draw_status_curse(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,20,80))
    draw.line([(s(c[0]-10),s(c[1]-8)),(s(c[0]+10),s(c[1]+8))], fill=(200,80,255), width=s(3))
    draw.line([(s(c[0]+10),s(c[1]-8)),(s(c[0]-10),s(c[1]+8))], fill=(200,80,255), width=s(3))

def draw_status_protect(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,40,100))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(100,160,240))
    draw.ellipse([(s(c[0]-6),s(c[1]-6)),(s(c[0]+6),s(c[1]+6))], fill=(60,120,200))

def draw_status_regen(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,80,20))
    draw.ellipse([(s(c[0]-8),s(c[1]-8)),(s(c[0]+8),s(c[1]+8))], fill=(100,220,100))
    draw.line([(s(c[0]-2),s(c[1])),(s(c[0]+10),s(c[1]))], fill=(0,180,40), width=s(3))

def draw_status_silence(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(40,40,60))
    draw.line([(s(c[0]-12),s(c[1]-8)),(s(c[0]+12),s(c[1]+8))], fill=(200,80,180), width=s(3))

def draw_alchemy_hp_plus(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(160,20,20))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(200,60,60))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(140,140,140))
    draw.polygon([(s(c[0]+2),s(c[1]+0)),(s(c[0]+2),s(c[1]-6)),(s(c[0]+8),s(c[1]-6)),
                  (s(c[0]+8),s(c[1]-3)),(s(c[0]+4),s(c[1]-3)),(s(c[0]+4),s(c[1]+0))], fill=(240,220,80))

def draw_alchemy_mp_plus(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,40,120))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(80,140,220))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(140,140,140))
    draw.polygon([(s(c[0]+2),s(c[1]+0)),(s(c[0]+2),s(c[1]-6)),(s(c[0]+8),s(c[1]-6)),
                  (s(c[0]+8),s(c[1]-3)),(s(c[0]+4),s(c[1]-3)),(s(c[0]+4),s(c[1]+0))], fill=(200,220,255))

def draw_alchemy_str(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(120,20,40))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(200,60,80))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(140,140,140))
    draw.line([(s(c[0]),s(c[1]+2)),(s(c[0]),s(c[1]+14))], fill=(220,180,40), width=s(2))
    draw.line([(s(c[0]-6),s(c[1]+8)),(s(c[0]+6),s(c[1]+8))], fill=(220,180,40), width=s(2))

def draw_alchemy_dex(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,100,40))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(80,200,100))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(140,140,140))
    draw.ellipse([(s(c[0]-3),s(c[1]+6)),(s(c[0]+3),s(c[1]+12))], fill=(180,240,140))

def draw_alchemy_res(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,80,100))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(160,170,190))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(120,120,130))

def draw_alchemy_oil(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,40,20))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(100,80,40))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(80,70,50))

def draw_alchemy_strong_poison(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,60,20))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(80,180,80))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(140,140,140))
    for i in range(3):
        draw.ellipse([(s(c[0]-(4+i)),s(c[1]+6+i)),(s(c[0]+(4+i)),s(c[1]+10+i))], fill=(160,220,140))

def draw_alchemy_antidote(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(40,40,60))
    draw.rectangle([(s(c[0]-8),s(c[1]-4)),(s(c[0]+8),s(c[1]+18))], fill=(230,240,250))
    draw.rectangle([(s(c[0]-4),s(c[1]-16)),(s(c[0]+4),s(c[1]-4))], fill=(180,190,200))

def draw_rune_def(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,40,100))
    draw.ellipse([(s(c[0]-12),s(c[1]-12)),(s(c[0]+12),s(c[1]+12))], fill=(60,120,220))
    draw.ellipse([(s(c[0]-8),s(c[1]-8)),(s(c[0]+8),s(c[1]+8))], fill=(140,180,255))

def draw_rune_atk(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(100,20,0))
    pts = [(s(c[0]),s(c[1]-14)),(s(c[0])+14,s(c[1])),(s(c[0]),s(c[1]+14)),(s(c[0])-14,s(c[1]))]
    draw.polygon(pts, fill=(240,80,40))

def draw_rune_alch(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(100,60,0))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(200,160,40))
    draw.line([(s(c[0]),s(c[1]-10)),(s(c[0]),s(c[1]+10))], fill=(140,100,20), width=s(2))
    draw.line([(s(c[0]-8),s(c[1])),(s(c[0]+8),s(c[1]))], fill=(140,100,20), width=s(2))

def draw_sigil_fire(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(120,20,0))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(255,140,0))
    draw.polygon([(s(c[0]),s(c[1]-8)),(s(c[0])+6,s(c[1]+4)),(s(c[0]),s(c[1]+8)),(s(c[0])-6,s(c[1]+4))], fill=(255,200,80))

def draw_sigil_ice(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,40,100))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(140,200,255))
    for i in range(6):
        a = i * (math.pi / 3.0)
        x2 = s(c[0] + int(math.cos(a) * 10))
        y2 = s(c[1] + int(math.sin(a) * 10))
        draw.line([(s(c[0]),s(c[1])),(x2,y2)], fill=(200,230,255), width=s(2))

def draw_sigil_poison(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,60,20))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(80,180,80))
    for i in range(3):
        y = s(c[1]-8+i*8)
        draw.line([(s(c[0]-8-i),y),(s(c[0]+8+i),y)], fill=(160,220,140), width=s(2))

def draw_sigil_light(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(120,80,0))
    draw.ellipse([(s(c[0]-12),s(c[1]-12)),(s(c[0]+12),s(c[1]+12))], fill=(255,220,80))
    draw.ellipse([(s(c[0]-6),s(c[1]-6)),(s(c[0]+6),s(c[1]+6))], fill=(255,240,140))

def draw_sigil_shadow(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,80,100))
    draw.polygon([(s(c[0]),s(c[1]-14)),(s(c[0])+10,s(c[1]+6)),(s(c[0])+4,s(c[1]+14)),
                  (s(c[0])-6,s(c[1]+10)),(s(c[0])-10,s(c[1]+2))], fill=(40,40,60))

def draw_combat_attack(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,20,20))
    pts = [(s(c[0]-4),s(c[1]+18)),(s(c[0]+4),s(c[1]+18)),(s(c[0]),s(c[1]-14))]
    draw.polygon(pts, fill=(200,200,210))
    draw.polygon([(s(c[0]),s(c[1]-14)),(s(c[0])+8,s(c[1]-12)),(s(c[0])+14,s(c[1]-8))], fill=(180,180,190))

def draw_combat_defend(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(20,60,100))
    draw.ellipse([(s(c[0]-12),s(c[1]-12)),(s(c[0]+12),s(c[1]+14))], fill=(140,160,200))
    draw.line([(s(c[0]),s(c[1]-12)),(s(c[0]),s(c[1]+14))], fill=(200,220,255), width=s(2))

def draw_combat_dodge(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(40,80,20))
    pts = [(s(c[0]+10),s(c[1]-12)),(s(c[0]+10),s(c[1]+12)),(s(c[0]-6),s(c[1]+4)),(s(c[0]+2),s(c[1]))]
    draw.polygon(pts, fill=(200,230,160))
    draw.polygon([(s(c[0]-6),s(c[1]+0)),(s(c[0]-14),s(c[1]-4)),(s(c[0]-14),s(c[1]+4))], fill=(160,200,100))

def draw_combat_counter(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(100,40,40))
    draw.line([(s(c[0]-12),s(c[1]-8)),(s(c[0]+12),s(c[1]+8))], fill=(200,200,220), width=s(3))
    draw.line([(s(c[0]+12),s(c[1]-8)),(s(c[0]-12),s(c[1]+8))], fill=(200,200,220), width=s(3))

def draw_combat_special(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,20,80))
    for i in range(8):
        a = i * (math.pi / 4.0)
        x2 = s(c[0] + int(math.cos(a) * 12))
        y2 = s(c[1] + int(math.sin(a) * 12))
        draw.line([(s(c[0]),s(c[1])),(x2,y2)], fill=(220,80,220), width=s(2))

def draw_combat_magic(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(40,20,80))
    draw.polygon([(s(c[0]),s(c[1]-12)),(s(c[0])+10,s(c[1]+4)),(s(c[0]),s(c[1]+12)),(s(c[0])-10,s(c[1]+4))], fill=(160,100,220))
    draw.ellipse([(s(c[0]-3),s(c[1]-2)),(s(c[0]+3),s(c[1]+2))], fill=(220,180,255))

def draw_combat_shoot(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,40,40))
    draw.polygon([(s(c[0]-14),s(c[1])),(s(c[0]-8),s(c[1]-3)),(s(c[0]-8),s(c[1]+3))], fill=(180,180,200))
    draw.line([(s(c[0]-14),s(c[1])),(s(c[0])+12,s(c[1]))], fill=(220,220,240), width=s(2))

def draw_combat_break(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,40,20))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(140,140,160))
    draw.line([(s(c[0]-6),s(c[1]-8)),(s(c[0])+4,s(c[1]+2))], fill=(255,80,80), width=s(2))
    draw.line([(s(c[0]-2),s(c[1]-10)),(s(c[0])+8,s(c[1]+4))], fill=(255,80,80), width=s(2))

def draw_damage_slash(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,20,20))
    draw.line([(s(c[0]-14),s(c[1]-4)),(s(c[0])+14,s(c[1]+4))], fill=(240,200,180), width=s(4))
    draw.line([(s(c[0]-12),s(c[1]+2)),(s(c[0])+12,s(c[1]-2))], fill=(240,200,180), width=s(4))

def draw_damage_pierce(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,40,20))
    draw.polygon([(s(c[0]),s(c[1]-14)),(s(c[0])+4,s(c[1]-8)),(s(c[0])+8,s(c[1]+8)),
                  (s(c[0])+4,s(c[1]+14)),(s(c[0]),s(c[1]+14)),(s(c[0])-4,s(c[1]+8)),(s(c[0])-4,s(c[1]-8))], fill=(200,220,190))

def draw_damage_blunt(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,40,20))
    draw.rectangle([(s(c[0]-8),s(c[1]-10)),(s(c[0]+8),s(c[1]+10))], fill=(180,170,150))

def draw_damage_fire(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(100,20,0))
    pts = [(s(c[0]),s(c[1]-14)),(s(c[0])+8,s(c[1]+4)),(s(c[0])-6,s(c[1]+6)),
           (s(c[0])+6,s(c[1]+10)),(s(c[0])-10,s(c[1]+12)),(s(c[0]),s(c[1]+8))]
    draw.polygon(pts, fill=(255,140,0))

def draw_system_map(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,60,30))
    draw.polygon([(s(c[0]-12),s(c[1]-10)),(s(c[0]+12),s(c[1]-8)),
                  (s(c[0]+12),s(c[1]+8)),(s(c[0]-12),s(c[1]+10))], fill=(200,170,120))
    draw.line([(s(c[0]-4),s(c[1]-6)),(s(c[0]+8),s(c[1]))], fill=(80,60,40), width=s(1))
    draw.line([(s(c[0]-4),s(c[1]+2)),(s(c[0]+8),s(c[1]+8))], fill=(80,60,40), width=s(1))

def draw_system_journal(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,60,40))
    draw.rectangle([(s(c[0]-10),s(c[1]-12)),(s(c[0]+10),s(c[1]+12))], fill=(200,170,120))
    draw.rectangle([(s(c[0]-10),s(c[1]-12)),(s(c[0]-8),s(c[1]+12))], fill=(60,40,20))
    draw.polygon([(s(c[0]+8),s(c[1]-12)),(s(c[0]+14),s(c[1]-6)),
                  (s(c[0]+14),s(c[1]+0)),(s(c[0]+8),s(c[1]+4))], fill=(200,170,120))

def draw_system_quests(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(80,40,20))
    draw.rectangle([(s(c[0]-10),s(c[1]-12)),(s(c[0]+10),s(c[1]+12))], fill=(220,200,160))
    draw.ellipse([(s(c[0]-8),s(c[1]+8)),(s(c[0]-4),s(c[1]+12))], fill=(160,40,40))
    for y in [-6,0,6]:
        draw.line([(s(c[0]-6),s(c[1]+y)),(s(c[0])+6,s(c[1]+y))], fill=(120,80,40), width=s(1))

def draw_system_settings(draw, c, color):
    draw.ellipse([(s(c[0]-16),s(c[1]-16)),(s(c[0]+16),s(c[1]+16))], fill=(60,40,20))
    draw.ellipse([(s(c[0]-10),s(c[1]-10)),(s(c[0]+10),s(c[1]+10))], fill=(100,80,40))
    for i in range(6):
        a = i * (math.pi / 3.0)
        x = s(c[0] + int(math.cos(a) * 12))
        y = s(c[1] + int(math.sin(a) * 12))
        draw.ellipse([(x-2, y-2), (x+2, y+2)], fill=(80,100,120))
    draw.ellipse([(s(c[0]-4),s(c[1]-4)),(s(c[0]+4),s(c[1]+4))], fill=(140,160,180))

fs_map = {
    "sword": draw_sword, "axe": draw_axe, "spear": draw_spear, "shield": draw_shield,
    "helmet": draw_helmet, "armor": draw_armor, "pouch": draw_pouch, "book": draw_book,
    "potion_red": (draw_potion, (200, 30, 30)),
    "potion_blue": (draw_potion, (30, 30, 200)),
    "potion_green": (draw_potion, (30, 200, 30)),
    "scroll": draw_scroll,
    "str": draw_stats_str, "dex": draw_stats_dex, "will": draw_stats_will, "know": draw_stats_know,
    "cha": draw_stats_cha, "luck": draw_stats_luck, "perception": draw_stats_perception, "endurance": draw_stats_endurance,
    "poison": draw_status_poison, "bleed": draw_status_bleed, "fear": draw_status_fear, "bless": draw_status_bless,
    "fatigue": draw_status_fatigue, "freeze": draw_status_freeze, "fire_status": draw_status_fire, "intox": draw_status_intox,
    "curse": draw_status_curse, "protect": draw_status_protect, "regen": draw_status_regen, "silence": draw_status_silence,
    "hp_plus": draw_alchemy_hp_plus, "mp_plus": draw_alchemy_mp_plus, "alchemy_str": draw_alchemy_str, "alchemy_dex": draw_alchemy_dex,
    "alchemy_res": draw_alchemy_res, "alchemy_oil": draw_alchemy_oil, "strong_poison": draw_alchemy_strong_poison, "alchemy_antidote": draw_alchemy_antidote,
    "rune_def": draw_rune_def, "rune_atk": draw_rune_atk, "rune_alch": draw_rune_alch,
    "sigil_fire": draw_sigil_fire, "sigil_ice": draw_sigil_ice, "sigil_poison": draw_sigil_poison,
    "sigil_light": draw_sigil_light, "sigil_shadow": draw_sigil_shadow,
    "combat_attack": draw_combat_attack, "combat_defend": draw_combat_defend, "combat_dodge": draw_combat_dodge, "combat_counter": draw_combat_counter,
    "combat_special": draw_combat_special, "combat_magic": draw_combat_magic, "combat_shoot": draw_combat_shoot,
    "combat_break": draw_combat_break,
    "damage_slash": draw_damage_slash, "damage_pierce": draw_damage_pierce, "damage_blunt": draw_damage_blunt, "damage_fire": draw_damage_fire,
    "system_map": draw_system_map, "system_journal": draw_system_journal, "system_quests": draw_system_quests, "system_settings": draw_system_settings
}

def get_fn(name):
    n = name.replace("ic_", "")
    if n in fs_map: return fs_map[n]

    parts = n.split("_", 1)
    if len(parts) > 1:
        base = parts[1]
        if base in fs_map: return fs_map[base]

        if n == "status_fire": return fs_map.get("fire_status")
        if n == "alchemy_str": return fs_map.get("alchemy_str")
        if n == "alchemy_dex": return fs_map.get("alchemy_dex")
        if n == "alchemy_res": return fs_map.get("alchemy_res")
        if n == "alchemy_oil": return fs_map.get("alchemy_oil")
        if n == "alchemy_antidote": return fs_map.get("alchemy_antidote")

    return None

def make_icon(fname):
    SIZE = 64
    img = SimpleImage((SIZE, SIZE), (30, 25, 20))
    d = SimpleDraw(img)
    res = get_fn(fname)
    if res:
        if isinstance(res, tuple):
            fn, col = res
        else:
            fn, col = res, None
        try:
            fn(d, (32, 32), col)
        except Exception as e:
            pass
    return img

os.makedirs("output/drawable", exist_ok=True)
for fname in filenames:
    img = make_icon(fname)
    img.save_ppm(f"output/drawable/{fname}.ppm")
    print(f"Generated: {fname}.ppm")
print(f"Done: {len(filenames)} icons")
