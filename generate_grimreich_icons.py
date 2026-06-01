from PIL import Image, ImageDraw, ImageFont, ImageFilter
import os, zipfile

# Manifest (64 plikow) - dokladne nazwy
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
    except Exception:
        f = ImageFont.load_default()
    bbox = f.getbbox(text)
    w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
    draw.text((cx - w/2, cy - h/2), text, font=f, fill=color)

# category -> draw function and color
category_draw = {
    "sword": (draw_sword, (200,200,210)),
    "shield": (draw_shield, (170,180,190)),
    "potion_red": (draw_potion, (200,60,60)),
    "potion_blue": (draw_potion, (60,100,200)),
    "potion_green": (draw_potion, (60,180,80)),
    "rune": (draw_rune, (180,140,255)),
    "flame": (draw_flame, (240,120,30)),
    "axe": (draw_sword, (190,190,200)),
    "spear": (draw_sword, (180,180,190)),
    "armor": (draw_shield, (160,170,180)),
    "helmet": (draw_shield, (150,160,170)),
    "pouch": (draw_potion, (160,120,80)),
    "book": (draw_shield, (140,100,60)),
    "scroll": (draw_potion, (200,180,120)),
    "default": (draw_rune, (200,200,200)),
}

def get_draw_fn(name):
    for key, val in category_draw.items():
        if key in name:
            return val
    return category_draw["default"]

def generate_icon(name, size=64):
    bg = (30, 25, 20)
    img = Image.new("RGBA", (size, size), bg)
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2
    scale = size / 64
    fn, color = get_draw_fn(name)
    try:
        fn(draw, cx, cy, scale, color)
    except TypeError:
        # text symbol fallback
        letter = name.replace("ic_","")[0].upper()
        draw_text_symbol(draw, cx, cy, scale, letter, color)
    # subtle vignette border
    draw.rectangle([(0,0),(size-1,size-1)], outline=(80,60,40), width=2)
    return img

os.makedirs("output/drawable", exist_ok=True)
for fname in filenames:
    img = generate_icon(fname)
    img.save(f"output/drawable/{fname}")
    print(f"Generated: {fname}")

print(f"Done: {len(filenames)} icons saved to output/drawable/")
