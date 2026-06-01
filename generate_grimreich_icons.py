from PIL import Image, ImageDraw, ImageFont, ImageFilter
import os, zipfile

# Manifest (64 plików) — dokładne nazwy
filenames = [
"ic_equipment_sword.png","ic_equipment_axe.png","ic_equipment_spear.png","ic_equipment_shield.png",
"ic_equipment_helmet.png","ic_equipment_armor.png","ic_equipment_pouch.png","ic_equipment_book.png",
"ic_equipment_potion_red.png","ic_equipment_potion_blue.png","ic_equipment_potion_green.png","ic_equipment_scroll.png",
"ic_stats_str.png","ic_stats_dex.png","ic_stats_will.png","ic_stats_know.png",
"ic_stats_cha.png","ic_stats_luck.png","ic_stats_perception.png","ic_stats_endurance.png",
"ic_status_poison.png","ic_status_bleed.png","ic_status_fear.png","ic_status_bless.png",
"ic_status_fatigue.png","ic_status_freeze.png","ic_status_fire.png","ic_status_intox.png",
"ic_status_curse.png","ic_status_protect.png","ic_status_regen.png","ic_status_silence.png",
"ic_alchemy_hp_plus.png","ic_alchemy_mp_plus.png","ic_alchemy_str.png","ic_alchemy_dex.png",
"ic_alchemy_res.png","ic_alchemy_oil.png","ic_alchemy_strong_poison.png","ic_alchemy_antidote.png",
"ic_rune_def.png","ic_rune_atk.png","ic_rune_alch.png","ic_sigil_fire.png",
"ic_sigil_ice.png","ic_sigil_poison.png","ic_sigil_light.png","ic_sigil_shadow.png",
"ic_combat_attack.png","ic_combat_defend.png","ic_combat_dodge.png","ic_combat_counter.png",
"ic_combat_special.png","ic_combat_magic.png","ic_combat_shoot.png","ic_combat_break.png",
"ic_damage_slash.png","ic_damage_pierce.png","ic_damage_blunt.png","ic_damage_fire.png",
"ic_system_map.png","ic_system_journal.png","ic_system_quests.png","ic_system_settings.png"
]

# Simple mapping of symbol types to draw functions (keeps icons readable)
def draw_sword(draw, cx, cy, scale, color):
    # blade
    draw.polygon([(cx-6*scale, cy+20*scale),(cx+6*scale, cy+20*scale),(cx+2*scale, cy-18*scale),(cx-2*scale, cy-18*scale)], fill=color)
    # hilt
    draw.rectangle([(cx-10*scale, cy+20*scale),(cx+10*scale, cy+26*scale)], fill=(80,60,40))
    draw.rectangle([(cx-2*scale, cy+26*scale),(cx+2*scale, cy+34*scale)], fill=(40,30,20))

def draw_shield(draw, cx, cy, scale, color):
    draw.ellipse([(cx-18*scale, cy-18*scale),(cx+18*scale, cy+18*scale)], fill=color)
    draw.line([(cx, cy-18*scale),(cx, cy+18*scale)], fill=(30,30,30), width=int(3*scale))

def draw_potion(draw, cx, cy, scale, color):
    draw.rectangle([(cx-10*scale, cy-6*scale),(cx+10*scale, cy+18*scale)], fill=color)
    draw.rectangle([(cx-6*scale, cy-18*scale),(cx+6*scale, cy-6*scale)], fill=(80,80,80))

def draw_rune(draw, cx, cy, scale, color):
    draw.line([(cx-12*scale, cy-12*scale),(cx+12*scale, cy+12*scale)], fill=color, width=int(3*scale))
    draw.line([(cx+12*scale, cy-12*scale),(cx-12*scale, cy+12*scale)], fill=color, width=int(3*scale))

def draw_flame(draw, cx, cy, scale, color):
    draw.polygon([(cx, cy-18*scale),(cx+10*scale, cy+6*scale),(cx, cy+12*scale),(cx-10*scale, cy+6*scale)], fill=color)

def draw_text_symbol(draw, cx, cy, scale, text, color):
    # fallback: draw a bold letter
    try:
        f = ImageFont.truetype("DejaVuSans-Bold.ttf", int(28*scale))
    except:
        f = ImageFont.load_default()
        bbox = f.getbbox(text); w,h = bbox[2]-bbox[0], bbox[3]-bbox[1]
    draw.text((cx-w/2, cy-h/2), text, font=f, fill=color)

# category -> draw function and color
category_draw = {
    "sword": (draw_sword, (200,200,210)),
    "shield": (draw_shield, (170,180,190)),
    "potion_red": (draw_potion, (200,60,60)),
    "potion_blue": (draw_potion, (60,120,200)),
    "potion_green": (draw_potion, (80,180,80)),
    "rune": (draw_rune, (180,140,60)),
    "flame": (draw_flame, (240,120,40)),
    "default": (draw_text_symbol, (220,220,220))
}

# simple heuristics to pick draw function by filename
def pick_drawer(name):
    if "sword" in name: return ("sword","S")
    if "axe" in name: return ("sword","A")  # reuse sword shape as heavy blade
    if "spear" in name: return ("sword","P")
    if "shield" in name: return ("shield","")
    if "potion_red" in name: return ("potion_red","")
    if "potion_blue" in name: return ("potion_blue","")
    if "potion_green" in name: return ("potion_green","")
    if "scroll" in name or "book" in name: return ("default","B")
    if "pouch" in name: return ("default","$") 
    if "str" in name: return ("default","STR")
    if "dex" in name: return ("default","DEX")
    if "will" in name: return ("default","W")
    if "know" in name: return ("default","K")
    if "poison" in name and "sigil" not in name: return ("potion_green","")
    if "bleed" in name: return ("default","♥")
    if "fire" in name and "sigil" in name: return ("flame","")
    if "sigil" in name or "rune" in name: return ("rune","")
    if "attack" in name: return ("sword","")
    if "defend" in name: return ("shield","")
    if "magic" in name: return ("rune","")
    if "map" in name: return ("default","M")
    if "journal" in name: return ("default","J")
    if "quests" in name: return ("default","Q")
    if "settings" in name: return ("default","⚙")
    if "damage" in name: return ("default","DMG")
    return ("default","?")

# create output dirs
out_dir = os.path.join("output","drawable")
os.makedirs(out_dir, exist_ok=True)

SIZE = 96
for fname in filenames:
    img = Image.new("RGBA", (SIZE, SIZE), (18,18,20,255))  # dark background
    draw = ImageDraw.Draw(img)

    # subtle vignette / texture
    overlay = Image.new("RGBA", (SIZE, SIZE), (0,0,0,0))
    od = ImageDraw.Draw(overlay)
    od.ellipse([(-20,-20),(SIZE+20,SIZE+20)], fill=(0,0,0,40))
    img = Image.alpha_composite(img, overlay)

    cat, label = pick_drawer(fname)
    drawer, color = category_draw.get(cat, category_draw["default"])

    cx, cy = SIZE//2, SIZE//2
    scale = 1.0

    # draw symbol
    if drawer == draw_text_symbol:
        drawer(draw, cx, cy, scale, label, color)
    else:
        drawer(draw, cx, cy, scale, color)

    # slight pixelation effect (optional): resize down and up to get blocky look
    small = img.resize((48,48), resample=Image.NEAREST)
    img = small.resize((SIZE,SIZE), Image.NEAREST)

    # save
    path = os.path.join(out_dir, fname)
    img.convert("RGBA").save(path, optimize=True)

# zip the folder
zip_name = "grimreich_icons.zip"
with zipfile.ZipFile(zip_name, "w", zipfile.ZIP_DEFLATED) as z:
    for root, _, files in os.walk(out_dir):
        for f in files:
            full = os.path.join(root, f)
            arcname = os.path.join("drawable", f)
            z.write(full, arcname)

print("Gotowe:", zip_name)
print("Pliki w:", out_dir)
