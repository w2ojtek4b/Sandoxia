import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sandboxia extends JPanel implements ActionListener {
    private static final int SZER = 120, WYS = 100, SKALA = 5;
    private static final int SZER_EKRANU = SZER * SKALA;
    private static final int WYS_EKRANU = WYS * SKALA;
    private static final double TEMP_POKOJOWA = 20.0;
    private static final double TEMP_MIN = -273.1;
    private static final double TEMP_WYBUCHU_TNT = 700.0;
    private static final int MAKS_DYSTANS_GAZU = 7;
    private static final int PROMIEN_WYBUCHU_BOMBY = 15;
    private static final int PROMIEN_WYBUCHU_TNT = 16;

    private static final int TICKI_NA_SEKUNDE = 30;
    private static final long NANOSEKUND_NA_TICK = 1_000_000_000L / TICKI_NA_SEKUNDE;

    private static final int PUSTE = 0, PIASEK = 1, WODA = 2, SCIANA = 3, ZIEMIA = 4,
            MOKRY_PIASEK = 5, BLOTO = 6, OGIEN = 7, PARA = 8, DYM = 9, LOD = 10, ZMARZLINA = 11,
            KWAS = 12, KAMIEN = 13, LAWA = 14, PIASKOWIEC = 15, BOMBA = 16, MUL = 17,
            BAZALT = 18, OBSYDIAN = 19, OLEJ = 20, TNT = 21, ZELAZO = 22, MAGNES = 23,
            PLYNNE_ZELAZO = 24, MIEDZ = 25, PLYNNA_MIEDZ = 26, ELEKTRYCZNOSC = 27,
            ANTY_BOMBA = 28, TLEN = 29, CO2 = 30, ROSLINA = 31;

    private static final int NARZ_OCIEPLANIE = -1, NARZ_MROZENIE = -2, NARZ_MIESZANIE = -3, NARZ_PRZESUN = -4;

    private static final int LICZBA_JEZYKOW = 10;
    private static final String[] KODY_JEZYKOW = {"PL", "EN_US", "EN_GB", "EN_AU", "ES", "ZH", "JA", "KO", "UK", "RU"};

    private int jezyk = 0;
    private int skalaTemperatury = 0;

    private int[][] siatka = new int[WYS][SZER];
    private int[][] czas = new int[WYS][SZER];
    private int[][] maxCzas = new int[WYS][SZER];
    private double[][] temp = new double[WYS][SZER];
    private Color[][] kolory = new Color[WYS][SZER];

    private int wybrany = PIASEK, pedzel = 3;
    private int myszX = -1, myszY = -1;
    private int popRysX = -1, popRysY = -1;
    private int popPrzesunX = -1, popPrzesunY = -1;
    private boolean przezroczysteGazyR = false;
    private boolean pauza = false;
    private long ostatniCzasNano = System.nanoTime();
    private long akumulatorNano = 0L;
    private long licznikTickow = 0L;
    private Random rand = new Random();
    private JLabel etykietaTemp;

    private JButton btnOcieplanie, btnMrozenie, btnMieszanie, btnPrzesun, btnUstawienia, btnGumka;
    private JLabel etykietaPedzla;
    private JSlider suwakPedzla;
    private JTabbedPane zakladki;
    private java.util.Map<Integer, JButton> przyciskiElementow = new java.util.HashMap<>();

    private static final Color[] BAZY = {
        Color.decode("#000000"), // kolor pustego
        Color.decode("#E5E68C"), // kolor piasku
        Color.decode("#3862C7"), // kolor wody
        Color.decode("#BABABA"), // kolor ściany
        Color.decode("#6E472B"), // kolor ziemi
        Color.decode("#A8A656"), // kolor mokrego piasku
        Color.decode("#422817"), // kolor błota
        Color.decode("#FF7A00"), // kolor ognia
        Color.decode("#DCE6F2"), // kolor pary wodnej
        Color.decode("#474747"), // kolor dymu
        Color.decode("#A8E6FF"), // kolor lodu
        Color.decode("#6E6B50"), // kolor zmarzliny
        Color.decode("#00BD03"), // kolor kwasu
        Color.decode("#7D7D7D"), // kolor kamienia
        Color.decode("#FF4500"), // kolor lawy
        Color.decode("#D9A665"), // kolor piaskowca
        Color.decode("#446E4C"), // kolor bomby
        Color.decode("#6E5438"), // kolor mułu
        Color.decode("#383245"), // kolor bazaltu
        Color.decode("#231936"), // kolor obsydianu
        Color.decode("#232536"), // kolor oleju
        Color.decode("#D43E37"), // kolor tnt
        Color.decode("#C6D4E8"), // kolor żelaza
        Color.decode("#1A7DE8"), // kolor magnesu
        Color.decode("#E85E42"), // kolor płynnego żelaza
        Color.decode("#E88D4A"), // kolor miedzi
        Color.decode("#F27538"), // kolor płynnej miedzi
        Color.decode("#FFE63B"), // kolor elektryczności
        Color.decode("#5F6C85"), // kolor anty-bomby
        Color.decode("#C4E3E8"), // kolor tlenu
        Color.decode("#33211F"), // kolor dwutlenku węgla
        Color.decode("#20BD17")  // kolor rośliny
    };

    private static final String[][] NAZWY_JEZYKI = {
        // 0: Polski
        {"Puste", "Piasek", "Woda", "Ściana", "Ziemia", "Mokry piasek",
         "Błoto", "Ogień", "Para wodna", "Dym", "Lód", "Zmarzlina",
         "Kwas", "Kamień", "Lawa", "Piaskowiec", "Bomba", "Muł",
         "Bazalt", "Obsydian", "Olej", "TNT", "Żelazo", "Magnes",
         "Płynne żelazo", "Miedź", "Płynna miedź", "Elektryczność",
         "Anty-bomba", "Tlen", "Dwutlenek węgla", "Roślina"},
        // 1: Angielski amerykański
        {"Empty", "Sand", "Water", "Wall", "Dirt", "Wet sand",
         "Mud", "Fire", "Steam", "Smoke", "Ice", "Permafrost",
         "Acid", "Rock", "Lava", "Sandstone", "Bomb", "Silt",
         "Basalt", "Obsidian", "Oil", "TNT", "Iron", "Magnet",
         "Molten iron", "Copper", "Molten copper", "Electricity",
         "Unbomb", "Oxygen", "Carbon dioxide", "Plant"},
        // 2: Angielski brytyjski
        {"Empty", "Sand", "Water", "Wall", "Dirt", "Wet sand",
         "Mud", "Fire", "Steam", "Smoke", "Ice", "Permafrost",
         "Acid", "Rock", "Lava", "Sandstone", "Bomb", "Silt",
         "Basalt", "Obsidian", "Oil", "TNT", "Iron", "Magnet",
         "Molten iron", "Copper", "Molten copper", "Electricity",
         "Unbomb", "Oxygen", "Carbon dioxide", "Plant"},
        // 3: Angielski australijski (z australijskim slangiem)
        {"Nada", "Sand", "Water", "Dunny wall", "Dirt", "Soggy sand",
         "Sludge", "Ripper fire", "Steam", "Smoko smoke", "Ice", "Permafrost",
         "Acid", "Rock", "Lava", "Sandstone", "Boomer", "Silt",
         "Basalt", "Obsidian", "Oil", "TNT", "Iron", "Magnet",
         "Molten iron", "Copper", "Molten copper", "Juice",
         "Anti-boomer", "Oxygen", "Carbon dioxide", "Bush tucker"},
        // 4: Hiszpański
        {"Vacío", "Arena", "Agua", "Muro", "Tierra", "Arena mojada",
         "Lodo", "Fuego", "Vapor", "Humo", "Hielo", "Permafrost",
         "Ácido", "Roca", "Lava", "Arenisca", "Bomba", "Limo",
         "Basalto", "Obsidiana", "Aceite", "TNT", "Hierro", "Imán",
         "Hierro fundido", "Cobre", "Cobre fundido", "Electricidad",
         "Antibomba", "Oxígeno", "Dióxido de carbono", "Planta"},
        // 5: Chiński
        {"空", "沙子", "水", "墙", "泥土", "湿沙",
         "泥浆", "火", "水蒸气", "烟", "冰", "永冻土",
         "酸", "石头", "熔岩", "砂岩", "炸弹", "淤泥",
         "玄武岩", "黑曜石", "油", "TNT", "铁", "磁铁",
         "熔融铁", "铜", "熔融铜", "电",
         "反炸弹", "氧气", "二氧化碳", "植物"},
        // 6: Japoński
        {"空", "砂", "水", "壁", "土", "濡れた砂",
         "泥", "炎", "水蒸気", "煙", "氷", "永久凍土",
         "酸", "石", "溶岩", "砂岩", "爆弾", "シルト",
         "玄武岩", "黒曜石", "油", "TNT", "鉄", "磁石",
         "溶融鉄", "銅", "溶融銅", "電気",
         "アンチ爆弾", "酸素", "二酸化炭素", "植物"},
        // 7: Koreański
        {"빈 공간", "모래", "물", "벽", "흙", "젖은 모래",
         "진흙", "불", "수증기", "연기", "얼음", "영구동토",
         "산", "돌", "용암", "사암", "폭탄", "실트",
         "현무암", "흑요석", "기름", "TNT", "철", "자석",
         "용융 철", "구리", "용융 구리", "전기",
         "안티 폭탄", "산소", "이산화탄소", "식물"},
        // 8: Ukraiński
        {"Порожньо", "Пісок", "Вода", "Стіна", "Земля", "Мокрий пісок",
         "Болото", "Вогонь", "Водяна пара", "Дим", "Лід", "Вічна мерзлота",
         "Кислота", "Камінь", "Лава", "Пісковик", "Бомба", "Мул",
         "Базальт", "Обсидіан", "Олія", "TNT", "Залізо", "Магніт",
         "Рідке залізо", "Мідь", "Рідка мідь", "Електрика",
         "Антибомба", "Кисень", "Вуглекислий газ", "Рослина"},
        // 9: Rosyjski
        {"Пусто", "Песок", "Вода", "Стена", "Земля", "Мокрый песок",
         "Грязь", "Огонь", "Водяной пар", "Дым", "Лёд", "Вечная мерзлота",
         "Кислота", "Камень", "Лава", "Песчаник", "Бомба", "Ил",
         "Базальт", "Обсидиан", "Масло", "TNT", "Железо", "Магнит",
         "Жидкое железо", "Медь", "Жидкая медь", "Электричество",
         "Антибомба", "Кислород", "Углекислый газ", "Растение"}
    };

    private static final String[][] ZAKLADKI_JEZYKI = {
        {"Naturalne", "Płyny", "Życie", "Solidne", "Gazy", "Energia", "Bronie", "Minerały"},
        {"Natural", "Liquids", "Life", "Solids", "Gases", "Energy", "Weapons", "Minerals"},
        {"Natural", "Liquids", "Life", "Solids", "Gases", "Energy", "Weapons", "Minerals"},
        {"Bush", "Liquids", "Critters & Flora", "Solids", "Gases", "Juice", "Boomers", "Minerals"},
        {"Natural", "Líquidos", "Vida", "Sólidos", "Gases", "Energía", "Armas", "Minerales"},
        {"自然", "液体", "生命", "固体", "气体", "能量", "武器", "矿物"},
        {"自然", "液体", "生命", "固体", "気体", "エネルギー", "武器", "鉱物"},
        {"자연", "액체", "생명", "고체", "기체", "에너지", "무기", "광물"},
        {"Природне", "Рідини", "Життя", "Тверді", "Гази", "Енергія", "Зброя", "Мінерали"},
        {"Природное", "Жидкости", "Жизнь", "Твёрдые", "Газы", "Энергия", "Оружие", "Минералы"}
    };

    private static final String[][] ETYKIETY_JEZYKOW_W_NAWIASACH = {
        {"polski", "angielski amerykański", "angielski brytyjski", "angielski australijski", "hiszpański", "chiński", "japoński", "koreański", "ukraiński", "rosyjski"},
        {"Polish", "American English", "British English", "Australian English", "Spanish", "Chinese", "Japanese", "Korean", "Ukrainian", "Russian"},
        {"Polish", "American English", "British English", "Australian English", "Spanish", "Chinese", "Japanese", "Korean", "Ukrainian", "Russian"},
        {"Polish", "Yank English", "Pom English", "Aussie English", "Spanish", "Chinese", "Jap English", "Korean", "Ukrainian", "Russian"},
        {"polaco", "inglés estadounidense", "inglés británico", "inglés australiano", "español", "chino", "japonés", "coreano", "ucraniano", "ruso"},
        {"波兰语", "美式英语", "英式英语", "澳式英语", "西班牙语", "中文", "日语", "韩语", "乌克兰语", "俄语"},
        {"ポーランド語", "アメリカ英語", "イギリス英語", "オーストラリア英語", "スペイン語", "中国語", "日本語", "韓国語", "ウクライナ語", "ロシア語"},
        {"폴란드어", "미국 영어", "영국 영어", "호주 영어", "스페인어", "중국어", "일본어", "한국어", "우크라이나어", "러시아어"},
        {"польська", "американська англійська", "британська англійська", "австралійська англійська", "іспанська", "китайська", "японська", "корейська", "українська", "російська"},
        {"польский", "американский английский", "британский английский", "австралийский английский", "испанский", "китайский", "японский", "корейский", "украинский", "русский"}
    };

    private static final String[] WLASNE_NAZWY_JEZYKOW = {
        "Polski",
        "English (US)",
        "English (UK)",
        "Strayan (AU)",
        "Español",
        "中文",
        "日本語",
        "한국어",
        "Українська",
        "Русский"
    };

    private final int[] ZAKRESY = {0, 14, 6, 14, 14, 12, 10, 12, 10, 10, 10, 12, 10, 12, 14, 10, 8, 10, 10, 10, 8, 8, 10, 6, 6, 10, 6, 8, 8, 6, 6, 10};
    private static final Font CZCIONKA = new Font("Cooper Black", Font.PLAIN, 12);

    private String etykietaOpcjiJezyka(int idxJezyka) {
        return WLASNE_NAZWY_JEZYKOW[idxJezyka] + " (" + ETYKIETY_JEZYKOW_W_NAWIASACH[jezyk][idxJezyka] + ")";
    }

    private String[] zbudujOpcjeJezykow() {
        String[] opcje = new String[LICZBA_JEZYKOW];
        for (int i = 0; i < LICZBA_JEZYKOW; i++) {
            opcje[i] = etykietaOpcjiJezyka(i);
        }
        return opcje;
    }

    private static String styl(String tekst) {
        String wynik = tekst.replaceAll("([ąćęłńóśźżĄĆĘŁŃÓŚŹŻáéíóúñÁÉÍÓÚÑ])", "<font face='Calibri' size='3'><b>$1</b></font>");
        return "<html><font face='Cooper Black'>" + wynik + "</font></html>";
    }

    private double ograniczTemp(double wartoscC) {
        return Math.max(TEMP_MIN, wartoscC);
    }

    private String formatujTemp(double wartoscC) {
        double clampedC = ograniczTemp(wartoscC);
        if (skalaTemperatury == 1) {
            double f = clampedC * 9.0 / 5.0 + 32.0;
            return String.format(java.util.Locale.US, "%.1f°F", f);
        }
        return String.format(java.util.Locale.US, "%.1f°C", clampedC);
    }

    private String tNazwa(int id) {
        return NAZWY_JEZYKI[jezyk][id];
    }

    private String tOcieplanie() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Warming";
            case "EN_AU" -> "Toasting up";
            case "ES" -> "Calentar";
            case "ZH" -> "加热";
            case "JA" -> "加温";
            case "KO" -> "가열";
            case "UK" -> "Нагрівання";
            case "RU" -> "Нагрев";
            default -> "Ocieplanie";
        };
    }

    private String tMrozenie() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Cooling";
            case "EN_AU" -> "Chilling out";
            case "ES" -> "Enfriar";
            case "ZH" -> "冷却";
            case "JA" -> "冷却";
            case "KO" -> "냉각";
            case "UK" -> "Охолодження";
            case "RU" -> "Охлаждение";
            default -> "Mrożenie";
        };
    }

    private String tMieszanie() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Stir";
            case "EN_AU" -> "Give it a stir";
            case "ES" -> "Mezclar";
            case "ZH" -> "搅拌";
            case "JA" -> "かき混ぜる";
            case "KO" -> "젓기";
            case "UK" -> "Перемішати";
            case "RU" -> "Перемешать";
            default -> "Mieszanie";
        };
    }

    private String tPrzesun() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Move";
            case "EN_AU" -> "Shift it";
            case "ES" -> "Mover";
            case "ZH" -> "移动";
            case "JA" -> "移動";
            case "KO" -> "이동";
            case "UK" -> "Пересунути";
            case "RU" -> "Переместить";
            default -> "Przesuń";
        };
    }

    private String tGumka() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Eraser";
            case "EN_AU" -> "Rubba";
            case "ES" -> "Borrador";
            case "ZH" -> "橡皮擦";
            case "JA" -> "消しゴム";
            case "KO" -> "지우개";
            case "UK" -> "Гумка";
            case "RU" -> "Ластик";
            default -> "Gumka";
        };
    }

    private String tPedzel() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Brush: ";
            case "EN_AU" -> "Mop size: ";
            case "ES" -> "Pincel: ";
            case "ZH" -> "画笔：";
            case "JA" -> "ブラシ: ";
            case "KO" -> "브러시: ";
            case "UK" -> "Пензель: ";
            case "RU" -> "Кисть: ";
            default -> "Pędzel: ";
        };
    }

    private String tNajedzTemp() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Hover over an element to see temperature";
            case "EN_AU" -> "Chuck the pointer over a bit to see the heat";
            case "ES" -> "Pasa el cursor sobre un elemento para ver la temperatura";
            case "ZH" -> "将鼠标悬停在元素上查看温度";
            case "JA" -> "要素にカーソルを合わせて温度を表示";
            case "KO" -> "요소 위에 커서를 올려 온도를 확인하세요";
            case "UK" -> "Наведіть курсор на елемент, щоб побачити температуру";
            case "RU" -> "Наведите курсор на элемент, чтобы увидеть температуру";
            default -> "Najedź kursorem, aby zobaczyć temperaturę";
        };
    }

    private String tTempLabel() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_AU" -> "Heat";
            case "ZH" -> "温度";
            case "JA" -> "温度";
            case "KO" -> "온도";
            case "UK" -> "Темп";
            case "RU" -> "Темп";
            default -> "Temp";
        };
    }

    private String dopisekPauzy() {
        if (!pauza) return "";
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> " [PAUSED]";
            case "EN_AU" -> " [HOLD YA HORSES]";
            case "ES" -> " [PAUSA]";
            case "ZH" -> " [已暂停]";
            case "JA" -> " [一時停止]";
            case "KO" -> " [일시정지]";
            case "UK" -> " [ПАУЗА]";
            case "RU" -> " [ПАУЗА]";
            default -> " [PAUZA]";
        };
    }

    private String dopisekTrybuR() {
        String rInfo = switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> przezroczysteGazyR ? " [R: Gas transparency ON]" : " [R: Gas transparency OFF]";
            case "EN_AU" -> przezroczysteGazyR ? " [R: See-through gas ON]" : " [R: See-through gas OFF]";
            case "ES" -> przezroczysteGazyR ? " [R: Transparencia de gas ON]" : " [R: Transparencia de gas OFF]";
            case "ZH" -> przezroczysteGazyR ? " [R: 气体透明 开]" : " [R: 气体透明 关]";
            case "JA" -> przezroczysteGazyR ? " [R: 気体透過 ON]" : " [R: 気体透過 OFF]";
            case "KO" -> przezroczysteGazyR ? " [R: 기체 투명도 켜짐]" : " [R: 기체 투명도 꺼짐]";
            case "UK" -> przezroczysteGazyR ? " [R: Прозорість газу УВІМК]" : " [R: Прозорість газу ВИМК]";
            case "RU" -> przezroczysteGazyR ? " [R: Прозрачность газа ВКЛ]" : " [R: Прозрачность газа ВЫКЛ]";
            default -> przezroczysteGazyR ? " [R: Przezroczystość gazu ON]" : " [R: Przezroczystość gazu OFF]";
        };
        return dopisekPauzy() + rInfo;
    }

    public Sandboxia() {
        Dimension rozmiarPlanszy = new Dimension(SZER_EKRANU, WYS_EKRANU);
        setPreferredSize(rozmiarPlanszy);
        setMinimumSize(rozmiarPlanszy);
        setMaximumSize(rozmiarPlanszy);
        setBackground(Color.BLACK);
        setFocusable(true);

        MouseAdapter m = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                aktualizujKursor(e);
                popRysX = myszX;
                popRysY = myszY;
                popPrzesunX = myszX;
                popPrzesunY = myszY;
                rysuj(e);
            }
            public void mouseReleased(MouseEvent e) {
                popRysX = -1;
                popRysY = -1;
                popPrzesunX = -1;
                popPrzesunY = -1;
            }
            public void mouseDragged(MouseEvent e) {
                aktualizujKursor(e);
                rysuj(e);
            }
            public void mouseMoved(MouseEvent e) {
                aktualizujKursor(e);
            }
            public void mouseExited(MouseEvent e) {
                myszX = -1;
                myszY = -1;
                popRysX = -1;
                popRysY = -1;
                popPrzesunX = -1;
                popPrzesunY = -1;
                odswiezEtykieteTemp();
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
        addMouseWheelListener(e -> {
            int rotacja = e.getWheelRotation();
            if (rotacja < 0) {
                ustawPedzel(pedzel + 1);
            } else if (rotacja > 0) {
                ustawPedzel(pedzel - 1);
            }
        });

        ostatniCzasNano = System.nanoTime();
        new Timer(10, this).start();
    }

    private void ustawPedzel(int nowyRozmiar) {
        int clamped = Math.max(1, Math.min(15, nowyRozmiar));
        if (clamped == pedzel) return;
        pedzel = clamped;
        if (suwakPedzla != null && suwakPedzla.getValue() != pedzel) {
            suwakPedzla.setValue(pedzel);
        }
        if (etykietaPedzla != null) {
            etykietaPedzla.setText(styl(tPedzel() + pedzel));
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        registerKeyboardAction(
            e -> przelaczTrybR(),
            KeyStroke.getKeyStroke(KeyEvent.VK_R, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        registerKeyboardAction(
            e -> przelaczPauze(),
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public void przelaczTrybR() {
        przezroczysteGazyR = !przezroczysteGazyR;
        odswiezEtykieteTemp();
        repaint();
    }

    public void przelaczPauze() {
        pauza = !pauza;
        ostatniCzasNano = System.nanoTime();
        akumulatorNano = 0L;
        odswiezEtykieteTemp();
        repaint();
    }

    public void ustawJezyk(int nowyJezyk) {
        if (nowyJezyk < 0 || nowyJezyk >= LICZBA_JEZYKOW) return;
        this.jezyk = nowyJezyk;
        odswiezTekstyUI();
    }

    public void ustawSkaleTemperatury(int nowaSkala) {
        if (nowaSkala < 0 || nowaSkala > 1) return;
        this.skalaTemperatury = nowaSkala;
        odswiezEtykieteTemp();
    }

    private void odswiezTekstyUI() {
        if (btnOcieplanie != null) btnOcieplanie.setText(styl(tOcieplanie()));
        if (btnMrozenie != null) btnMrozenie.setText(styl(tMrozenie()));
        if (btnMieszanie != null) btnMieszanie.setText(styl(tMieszanie()));
        if (btnPrzesun != null) btnPrzesun.setText(styl(tPrzesun()));
        if (btnGumka != null) btnGumka.setText(styl(tGumka()));
        if (etykietaPedzla != null) etykietaPedzla.setText(styl(tPedzel() + pedzel));

        for (java.util.Map.Entry<Integer, JButton> entry : przyciskiElementow.entrySet()) {
            entry.getValue().setText(styl(tNazwa(entry.getKey())));
        }

        if (zakladki != null) {
            for (int i = 0; i < zakladki.getTabCount(); i++) {
                zakladki.setTitleAt(i, styl(ZAKLADKI_JEZYKI[jezyk][i]));
            }
        }

        odswiezEtykieteTemp();
    }

    private String tytulUstawien() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Settings";
            case "EN_AU" -> "Gear Settings";
            case "ES" -> "Ajustes";
            case "ZH" -> "设置";
            case "JA" -> "設定";
            case "KO" -> "설정";
            case "UK" -> "Налаштування";
            case "RU" -> "Настройки";
            default -> "Ustawienia";
        };
    }

    private String tekstEtykietyJezyka() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Language:";
            case "EN_AU" -> "Lingo:";
            case "ES" -> "Idioma:";
            case "ZH" -> "语言：";
            case "JA" -> "言語: ";
            case "KO" -> "언어: ";
            case "UK" -> "Мова:";
            case "RU" -> "Язык:";
            default -> "Język:";
        };
    }

    private String tekstEtykietySkali() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Temperature scale:";
            case "EN_AU" -> "Heat scale:";
            case "ES" -> "Escala de temperatura:";
            case "ZH" -> "温度单位：";
            case "JA" -> "温度スケール: ";
            case "KO" -> "온도 단위: ";
            case "UK" -> "Шкала температури:";
            case "RU" -> "Шкала температуры:";
            default -> "Skala temperatury:";
        };
    }

    private String[] opcjeSkali() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB", "EN_AU" -> new String[]{"Metric (°C)", "Imperial (°F)"};
            case "ES" -> new String[]{"Métrico (°C)", "Imperial (°F)"};
            case "ZH" -> new String[]{"公制 (°C)", "英制 (°F)"};
            case "JA" -> new String[]{"メートル法 (°C)", "華氏 (°F)"};
            case "KO" -> new String[]{"섭씨 (°C)", "화씨 (°F)"};
            case "UK" -> new String[]{"Метрична (°C)", "Імперська (°F)"};
            case "RU" -> new String[]{"Метрическая (°C)", "Имперская (°F)"};
            default -> new String[]{"Metryczny (°C)", "Imperialny (°F)"};
        };
    }

    private String tekstZamknij() {
        return switch (KODY_JEZYKOW[jezyk]) {
            case "EN_US", "EN_GB" -> "Close";
            case "EN_AU" -> "Sweet, close it";
            case "ES" -> "Cerrar";
            case "ZH" -> "关闭";
            case "JA" -> "閉じる";
            case "KO" -> "닫기";
            case "UK" -> "Закрити";
            case "RU" -> "Закрыть";
            default -> "Zamknij";
        };
    }

    private void otworzOknoUstawien() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), tytulUstawien(), Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridLayout(2, 1, 6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        JPanel wierszJezyk = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        JLabel lbl = new JLabel(styl(tekstEtykietyJezyka()));
        lbl.setFont(CZCIONKA);

        JComboBox<String> combo = new JComboBox<>(new DefaultComboBoxModel<>(zbudujOpcjeJezykow()));
        combo.setFont(CZCIONKA);
        combo.setSelectedIndex(jezyk);

        JPanel wierszSkala = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        JLabel lblSkala = new JLabel(styl(tekstEtykietySkali()));
        lblSkala.setFont(CZCIONKA);

        JComboBox<String> comboSkala = new JComboBox<>(new DefaultComboBoxModel<>(opcjeSkali()));
        comboSkala.setFont(CZCIONKA);
        comboSkala.setSelectedIndex(skalaTemperatury);
        comboSkala.addActionListener(e -> ustawSkaleTemperatury(comboSkala.getSelectedIndex()));

        JButton zamknij = new JButton(styl(tekstZamknij()));
        zamknij.setFont(CZCIONKA);
        zamknij.addActionListener(e -> dialog.dispose());

        combo.addActionListener(e -> {
            int wybranyJezyk = combo.getSelectedIndex();
            ustawJezyk(wybranyJezyk);
            dialog.setTitle(tytulUstawien());
            lbl.setText(styl(tekstEtykietyJezyka()));
            lblSkala.setText(styl(tekstEtykietySkali()));
            zamknij.setText(styl(tekstZamknij()));

            int zachowanaSkala = comboSkala.getSelectedIndex();
            comboSkala.setModel(new DefaultComboBoxModel<>(opcjeSkali()));
            comboSkala.setSelectedIndex(zachowanaSkala);

            combo.setModel(new DefaultComboBoxModel<>(zbudujOpcjeJezykow()));
            combo.setSelectedIndex(wybranyJezyk);
        });

        wierszJezyk.add(lbl);
        wierszJezyk.add(combo);

        wierszSkala.add(lblSkala);
        wierszSkala.add(comboSkala);

        panel.add(wierszJezyk);
        panel.add(wierszSkala);

        JPanel dolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dolPanel.add(zamknij);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(dolPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        requestFocusInWindow();
    }

    private void aktualizujKursor(MouseEvent e) {
        int sx = e.getX() / SKALA;
        int sy = e.getY() / SKALA;
        if (wGranicach(sx, sy)) {
            myszX = sx;
            myszY = sy;
        } else {
            myszX = -1;
            myszY = -1;
        }
        odswiezEtykieteTemp();
    }

    private void odswiezEtykieteTemp() {
        if (etykietaTemp == null) return;
        if (wGranicach(myszX, myszY)) {
            int t = siatka[myszY][myszX];
            if (t == PUSTE) {
                etykietaTemp.setText(styl(tNazwa(PUSTE) + dopisekTrybuR()));
            } else {
                String opis = tNazwa(t) + " | " + tTempLabel() + ": " + formatujTemp(temp[myszY][myszX]) + dopisekTrybuR();
                etykietaTemp.setText(styl(opis));
            }
        } else {
            etykietaTemp.setText(styl(tNajedzTemp() + dopisekTrybuR()));
        }
    }

    private Color odcien(Color b, int z) {
        if (z <= 0) return b;
        int d = rand.nextInt(z * 2 + 1) - z;
        return new Color(
            Math.max(0, Math.min(255, b.getRed() + d)),
            Math.max(0, Math.min(255, b.getGreen() + d)),
            Math.max(0, Math.min(255, b.getBlue() + d))
        );
    }

    // kolor magnesu
    private Color kolorMagnesu() {
        Color baza = rand.nextBoolean() ? Color.decode("#1A7DE8") : Color.decode("#E81D1D");
        return odcien(baza, ZAKRESY[MAGNES]);
    }

    // kolor zwykłego gorącego
    private Color kolorZwykleGorace(double temperatura, double progCzerwony, double progPomaranczowy, double progZolty, double progBialy) {
        if (temperatura >= progBialy) {
            float p = (float) Math.max(0.0, Math.min(1.0, (progBialy + 250.0 - temperatura) / 250.0));
            int gb = (int) (255 - p * 15);
            return odcien(new Color(255, gb, gb), 4);
        } else if (temperatura >= progZolty) {
            float p = (float) ((progBialy - temperatura) / Math.max(1.0, progBialy - progZolty));
            int b = (int) Math.max(0, Math.min(240, (1.0f - p) * 240));
            return odcien(new Color(255, 255, b), 6);
        } else if (temperatura >= progPomaranczowy) {
            float p = (float) ((progZolty - temperatura) / Math.max(1.0, progZolty - progPomaranczowy));
            int g = (int) (255 - p * 125);
            return odcien(new Color(255, g, 0), 8);
        } else if (temperatura >= progCzerwony) {
            float p = (float) ((progPomaranczowy - temperatura) / Math.max(1.0, progPomaranczowy - progCzerwony));
            int g = (int) (130 * (1.0f - p));
            return odcien(new Color(255, g, 0), 7);
        } else {
            float p = (float) Math.max(0.0, Math.min(1.0, (progCzerwony - temperatura) / 80.0));
            int r = (int) (220 - p * 95);
            return odcien(new Color(r, 0, 0), 5);
        }
    }

    // kolor metalicznego gorącego
    private Color kolorMetaliczneGorace(double temperatura, double tempBazowa, double tempCiemnaCzerwien, double tempZolta, double tempBiala, int zakresOdcienia) {
        if (temperatura >= tempBiala) {
            float p = (float) Math.max(0.0, Math.min(1.0, (tempBiala + 250.0 - temperatura) / 250.0));
            int r = (int) (252 + p * 3);
            int g = (int) (242 - p * 18);
            int b = (int) (218 - p * 55);
            return odcien(new Color(r, g, b), 4);
        } else if (temperatura >= tempZolta) {
            float p = (float) ((tempBiala - temperatura) / Math.max(1.0, tempBiala - tempZolta));
            int r = (int) (252 - p * 6);
            int g = (int) (224 - p * 44);
            int b = (int) (163 - p * 95);
            return odcien(new Color(r, g, b), 5);
        } else if (temperatura >= tempBazowa) {
            float p = (float) ((temperatura - tempBazowa) / Math.max(1.0, tempZolta - tempBazowa));
            int r = (int) (232 + p * 14);
            int g = (int) (94 + p * 86);
            int b = (int) (66 + p * 2);
            return odcien(new Color(r, g, b), zakresOdcienia);
        } else {
            float p = (float) Math.max(0.0, Math.min(1.0, (tempBazowa - temperatura) / Math.max(1.0, tempBazowa - tempCiemnaCzerwien)));
            int r = (int) (232 - p * 68);
            int g = (int) (94 - p * 60);
            int b = (int) (66 - p * 32);
            return odcien(new Color(r, g, b), zakresOdcienia);
        }
    }

    // kolor ognia
    private Color kolorOgnia(double temperatura) {
        return kolorZwykleGorace(temperatura, 100.0, 650.0, 900.0, 1150.0);
    }

    // kolor lawy
    private Color kolorLawy(double temperatura) {
        return kolorZwykleGorace(temperatura, 1040.0, 1100.0, 1250.0, 1450.0);
    }

    // kolor płynnego żelaza
    private Color kolorPlynnegoZelaza(double temperatura) {
        return kolorMetaliczneGorace(temperatura, 1800.0, 1580.0, 2050.0, 2300.0, ZAKRESY[PLYNNE_ZELAZO]);
    }

    // kolor płynnej miedzi
    private Color kolorPlynnejMiedzi(double temperatura) {
        return kolorMetaliczneGorace(temperatura, 1250.0, 1080.0, 1500.0, 1750.0, ZAKRESY[PLYNNA_MIEDZ]);
    }

    // kolor elektryczności
    private Color kolorElektrycznosci() {
        Color baza = rand.nextBoolean() ? Color.decode("#FFE63B") : Color.decode("#7CE8FF");
        return odcien(baza, ZAKRESY[ELEKTRYCZNOSC]);
    }

    // kolor anty-bomby
    private Color kolorAntyBomby() {
        return odcien(BAZY[ANTY_BOMBA], ZAKRESY[ANTY_BOMBA]);
    }

    private float wspolczynnikJasnosciEkranaTroll() {
        Window okno = SwingUtilities.getWindowAncestor(this);
        if (okno == null || !okno.isShowing()) return 1.0f;
        Color bg = okno.getBackground();
        if (bg == null) return 1.0f;
        float jasnoscOkna = (0.299f * bg.getRed() + 0.587f * bg.getGreen() + 0.114f * bg.getBlue()) / 255.0f;
        if (!okno.isActive()) {
            return 0.08f;
        }
        return Math.max(0.05f, Math.min(1.0f, 0.15f + jasnoscOkna * 0.85f));
    }

    private int losujMaterialPoStygnięciuLawy() {
        double los = rand.nextDouble();
        if (los < 0.40) {
            return KAMIEN;
        } else if (los < 0.99) {
            return BAZALT;
        } else {
            return OBSYDIAN;
        }
    }

    private int losujMaterialPoKontakcieZWoda() {
        double los = rand.nextDouble();
        if (los < 0.20) {
            return OBSYDIAN;
        } else if (los < 0.50) {
            return KAMIEN;
        } else {
            return BAZALT;
        }
    }

    private void sprawdzOgienPonizej100(int x, int y) {
        if (siatka[y][x] == OGIEN && temp[y][x] < 100.0) {
            ustawZTemp(x, y, DYM, Math.max(25.0, temp[y][x]));
        }
    }

    private void aktualizujKolorPoRecznejZmianieTemp(int x, int y) {
        int t = siatka[y][x];
        if (t == OGIEN) {
            sprawdzOgienPonizej100(x, y);
            if (siatka[y][x] == OGIEN) {
                kolory[y][x] = kolorOgnia(temp[y][x]);
            }
        } else if (t == LAWA) {
            kolory[y][x] = kolorLawy(temp[y][x]);
        } else if (t == PLYNNE_ZELAZO) {
            kolory[y][x] = kolorPlynnegoZelaza(temp[y][x]);
        } else if (t == PLYNNA_MIEDZ) {
            kolory[y][x] = kolorPlynnejMiedzi(temp[y][x]);
        }
    }

    private double domyslnaTemp(int t) {
        switch (t) {
            case OGIEN: return 900.0;
            case LOD: return -5.0;
            case ZMARZLINA: return -25.0;
            case PARA: return 115.0;
            case LAWA: return 1100.0;
            case PLYNNE_ZELAZO: return 1800.0;
            case PLYNNA_MIEDZ: return 1250.0;
            case ELEKTRYCZNOSC: return 120.0;
            default: return TEMP_POKOJOWA;
        }
    }

    private void ustaw(int x, int y, int t) {
        ustawZTemp(x, y, t, domyslnaTemp(t));
    }

    private void ustawZTemp(int x, int y, int t, double nowaTemp) {
        if (!wGranicach(x, y)) return;

        siatka[y][x] = t;
        temp[y][x] = (t == PUSTE) ? 0.0 : ograniczTemp(nowaTemp);
        int dl;
        if (t == OGIEN) {
            dl = 180 + rand.nextInt(120);
        } else if (t == DYM) {
            dl = 180 + rand.nextInt(180);
        } else if (t == ELEKTRYCZNOSC) {
            dl = 6 + rand.nextInt(7);
        } else {
            dl = 0;
        }
        czas[y][x] = dl;
        maxCzas[y][x] = dl;

        if (t == PUSTE) {
            kolory[y][x] = null;
        } else if (t == OGIEN) {
            kolory[y][x] = kolorOgnia(temp[y][x]);
        } else if (t == LAWA) {
            kolory[y][x] = kolorLawy(temp[y][x]);
        } else if (t == PLYNNE_ZELAZO) {
            kolory[y][x] = kolorPlynnegoZelaza(temp[y][x]);
        } else if (t == PLYNNA_MIEDZ) {
            kolory[y][x] = kolorPlynnejMiedzi(temp[y][x]);
        } else if (t == ELEKTRYCZNOSC) {
            kolory[y][x] = kolorElektrycznosci();
        } else if (t == MAGNES) {
            kolory[y][x] = kolorMagnesu();
        } else if (t == ANTY_BOMBA) {
            kolory[y][x] = kolorAntyBomby();
        } else {
            kolory[y][x] = odcien(BAZY[t], ZAKRESY[t]);
        }
    }

    private void wybuchEksplozywny(int cx, int cy, int promien, boolean niszczySciane) {
        int rMaxOgien = promien + 3;

        for (int dy = -rMaxOgien; dy <= rMaxOgien; dy++) {
            for (int dx = -rMaxOgien; dx <= rMaxOgien; dx++) {
                int x = cx + dx;
                int y = cy + dy;
                if (!wGranicach(x, y)) continue;
                if (siatka[y][x] == SCIANA && !niszczySciane) continue;

                double dist = Math.hypot(dx, dy);
                if (dist <= promien) {
                    if (rand.nextFloat() < 0.16f) {
                        ustawZTemp(x, y, OGIEN, 900.0);
                    } else {
                        ustawZTemp(x, y, PUSTE, 0.0);
                    }
                } else if (dist <= rMaxOgien) {
                    if (siatka[y][x] == PUSTE || rand.nextFloat() < 0.55f) {
                        ustawZTemp(x, y, OGIEN, 900.0);
                    }
                }
            }
        }
    }

    private void wybuchAntyBomby(int cx, int cy, int promien) {
        List<Integer> znalezioneTypy = new ArrayList<>();
        List<Double> znalezioneTempy = new ArrayList<>();

        for (int dy = -promien; dy <= promien; dy++) {
            for (int dx = -promien; dx <= promien; dx++) {
                int x = cx + dx;
                int y = cy + dy;
                if (!wGranicach(x, y)) continue;
                if (Math.hypot(dx, dy) <= promien) {
                    int t = siatka[y][x];
                    if (t != PUSTE && t != ANTY_BOMBA && t != SCIANA) {
                        znalezioneTypy.add(t);
                        znalezioneTempy.add(temp[y][x]);
                    }
                }
            }
        }

        ustawZTemp(cx, cy, PUSTE, 0.0);
        if (znalezioneTypy.isEmpty()) return;

        for (int dy = -promien; dy <= promien; dy++) {
            for (int dx = -promien; dx <= promien; dx++) {
                int x = cx + dx;
                int y = cy + dy;
                if (!wGranicach(x, y)) continue;
                if (Math.hypot(dx, dy) <= promien && siatka[y][x] == PUSTE && rand.nextFloat() < 0.65f) {
                    int idx = rand.nextInt(znalezioneTypy.size());
                    ustawZTemp(x, y, znalezioneTypy.get(idx), znalezioneTempy.get(idx));
                }
            }
        }
    }

    private void wybuchBomby(int cx, int cy) {
        wybuchEksplozywny(cx, cy, PROMIEN_WYBUCHU_BOMBY, false);
    }

    private void wybuchTNT(int cx, int cy) {
        wybuchEksplozywny(cx, cy, PROMIEN_WYBUCHU_TNT, true);
    }

    private boolean czyTntPowinienWybuchnac(int x, int y) {
        if (siatka[y][x] != TNT) return false;
        if (temp[y][x] >= TEMP_WYBUCHU_TNT) return true;

        int[] dxS = {0, -1, 1, 0};
        int[] dyS = {1, 0, 0, -1};
        for (int i = 0; i < 4; i++) {
            int nx = x + dxS[i];
            int ny = y + dyS[i];
            if (wGranicach(nx, ny) && (siatka[ny][nx] == OGIEN || siatka[ny][nx] == ELEKTRYCZNOSC)) {
                return true;
            }
        }
        return false;
    }

    private boolean bombaDotykaCzegos(int x, int y) {
        if (y + 1 >= WYS) return true;
        int[] dxS = {0, -1, 1, 0};
        int[] dyS = {1, 0, 0, -1};
        for (int i = 0; i < 4; i++) {
            int nx = x + dxS[i];
            int ny = y + dyS[i];
            if (wGranicach(nx, ny) && siatka[ny][nx] != PUSTE && siatka[ny][nx] != BOMBA && siatka[ny][nx] != ANTY_BOMBA) {
                return true;
            }
        }
        return false;
    }

    private boolean czyPrawyPrzycisk(MouseEvent e) {
        return SwingUtilities.isRightMouseButton(e)
            || (e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0
            || e.getButton() == MouseEvent.BUTTON3;
    }

    private boolean czyGaz(int t) {
        return t == PARA || t == DYM || t == TLEN || t == CO2;
    }

    private boolean czyGazLosowy(int t) {
        return t == TLEN || t == CO2;
    }

    private boolean czyPrzyciaganePrzezMagnes(int t) {
        return t == ZELAZO || t == MIEDZ;
    }

    private boolean czyMoznaPrzeniescNarzedziem(int x, int y, List<Point> magnesy) {
        int t = siatka[y][x];
        if (t == PUSTE || t == SCIANA || t == KAMIEN || t == BAZALT || t == OBSYDIAN
                || t == PIASKOWIEC || t == LOD || t == ZMARZLINA || t == MAGNES || t == ROSLINA) {
            return false;
        }
        if (czyPrzyciaganePrzezMagnes(t)) {
            if (magnesy.isEmpty()) return false;
            Point mag = najblizszyMagnes(x, y, magnesy);
            if (mag == null) return false;
            return Math.hypot(mag.x - x, mag.y - y) <= 28.0 || czas[y][x] > 0;
        }
        return true;
    }

    private List<Point> znajdzMagnesy() {
        List<Point> magnesy = new ArrayList<>();
        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                if (siatka[y][x] == MAGNES) {
                    magnesy.add(new Point(x, y));
                }
            }
        }
        return magnesy;
    }

    private Point najblizszyMagnes(int x, int y, List<Point> magnesy) {
        Point best = null;
        double bestDist = Double.MAX_VALUE;
        for (Point m : magnesy) {
            double d = Math.hypot(m.x - x, m.y - y);
            if (d < bestDist) {
                bestDist = d;
                best = m;
            }
        }
        return best;
    }

    private boolean przyciagnijMetalDoMagnesu(int x, int y, List<Point> magnesy, boolean[][] akt) {
        if (magnesy.isEmpty()) {
            czas[y][x] = 0;
            return false;
        }

        Point mag = najblizszyMagnes(x, y, magnesy);
        if (mag == null) {
            czas[y][x] = 0;
            return false;
        }

        double dist = Math.hypot(mag.x - x, mag.y - y);
        if (dist <= 1.5) {
            czas[y][x] = 0;
            return false;
        }

        int ped = Math.min(20, czas[y][x] + 1);
        czas[y][x] = ped;

        int krokCoIle = (ped < 5) ? 3 : ((ped < 12) ? 2 : 1);
        if ((licznikTickow + x + y) % krokCoIle != 0) {
            return true;
        }

        int dx = Integer.compare(mag.x, x) + (rand.nextInt(3) - 1);
        int dy = Integer.compare(mag.y, y) + (rand.nextInt(3) - 1);
        dx = Math.max(-1, Math.min(1, dx));
        dy = Math.max(-1, Math.min(1, dy));

        int[][] proby = {
            {dx, dy},
            {dx, 0},
            {0, dy},
            {rand.nextInt(3) - 1, rand.nextInt(3) - 1}
        };

        for (int[] p : proby) {
            int nx = x + p[0];
            int ny = y + p[1];
            if ((nx != x || ny != y) && wolne(nx, ny)) {
                zamien(x, y, nx, ny);
                akt[ny][nx] = true;
                return true;
            }
        }

        return false;
    }

    private float alphaPoOdleglosciIRozmiarzeGrupy(int x, int y, int typGazu) {
        double najblizszyDystans = MAKS_DYSTANS_GAZU + 1.0;
        int sasiedziWGrupie = 0;
        int promienGrupy = 3;

        for (int dy = -MAKS_DYSTANS_GAZU; dy <= MAKS_DYSTANS_GAZU; dy++) {
            for (int dx = -MAKS_DYSTANS_GAZU; dx <= MAKS_DYSTANS_GAZU; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (wGranicach(nx, ny) && siatka[ny][nx] == typGazu) {
                    double d = Math.hypot(dx, dy);
                    if (d < najblizszyDystans) {
                        najblizszyDystans = d;
                    }
                    if (Math.abs(dx) <= promienGrupy && Math.abs(dy) <= promienGrupy) {
                        sasiedziWGrupie++;
                    }
                }
            }
        }

        double maxSasiedzi = (promienGrupy * 2 + 1.0) * (promienGrupy * 2 + 1.0) - 1.0;
        double wspolczynnikGrupy = Math.min(1.0, sasiedziWGrupie / maxSasiedzi);

        double ratioDystansu;
        if (najblizszyDystans <= 1.05) {
            ratioDystansu = 0.0;
        } else {
            ratioDystansu = Math.min(1.0, (najblizszyDystans - 1.05) / (MAKS_DYSTANS_GAZU - 1.05));
        }

        double przezroczystosc = ratioDystansu * (1.0 - wspolczynnikGrupy * 0.65);
        float minAlpha = 0.22f;
        return (float) (1.0 - przezroczystosc * (1.0 - minAlpha));
    }

    private void mieszajWPedzlu(int sx, int sy, int r) {
        List<Point> niepuste = new ArrayList<>();
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                int x = sx + dx, y = sy + dy;
                if (dx * dx + dy * dy <= r * r && wGranicach(x, y) && siatka[y][x] != PUSTE) {
                    niepuste.add(new Point(x, y));
                }
            }
        }
        if (niepuste.size() < 2) return;

        int liczbaZamian = Math.max(1, niepuste.size() / 2);
        for (int i = 0; i < liczbaZamian; i++) {
            Point a = niepuste.get(rand.nextInt(niepuste.size()));
            Point b = niepuste.get(rand.nextInt(niepuste.size()));
            if (a.x != b.x || a.y != b.y) {
                zamien(a.x, a.y, b.x, b.y);
            }
        }
    }

    private void przesuwajWPedzlu(int sx, int sy, int r, int deltaX, int deltaY) {
        if (deltaX == 0 && deltaY == 0) return;
        List<Point> magnesy = znajdzMagnesy();
        boolean[][] przesuniete = new boolean[WYS][SZER];

        int startY = (deltaY > 0) ? sy + r : sy - r;
        int endY = (deltaY > 0) ? sy - r : sy + r;
        int stepY = (deltaY > 0) ? -1 : 1;

        int startX = (deltaX > 0) ? sx + r : sx - r;
        int endX = (deltaX > 0) ? sx - r : sx + r;
        int stepX = (deltaX > 0) ? -1 : 1;

        for (int y = startY; y != endY + stepY; y += stepY) {
            for (int x = startX; x != endX + stepX; x += stepX) {
                if (!wGranicach(x, y)) continue;
                int dx = x - sx, dy = y - sy;
                if (dx * dx + dy * dy > r * r) continue;
                if (przesuniete[y][x] || !czyMoznaPrzeniescNarzedziem(x, y, magnesy)) continue;

                int nx = x + deltaX;
                int ny = y + deltaY;
                if (wGranicach(nx, ny) && !przesuniete[ny][nx] && siatka[ny][nx] == PUSTE) {
                    zamien(x, y, nx, ny);
                    przesuniete[ny][nx] = true;
                }
            }
        }
    }

    private void rysujPunkt(int sx, int sy, int r, boolean gumka) {
        if (!gumka && wybrany == NARZ_MIESZANIE) {
            mieszajWPedzlu(sx, sy, r);
            return;
        }

        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                int x = sx + dx, y = sy + dy;
                if (dx * dx + dy * dy <= r * r && wGranicach(x, y)) {
                    if (gumka) {
                        ustaw(x, y, PUSTE);
                    } else if (siatka[y][x] == PUSTE) {
                        if (wybrany == OGIEN) {
                            ustawZTemp(x, y, OGIEN, 900.0);
                        } else if (wybrany != NARZ_OCIEPLANIE && wybrany != NARZ_MROZENIE && wybrany != NARZ_MIESZANIE && wybrany != NARZ_PRZESUN) {
                            ustaw(x, y, wybrany);
                        }
                    } else {
                        if (wybrany == NARZ_OCIEPLANIE) {
                            temp[y][x] = ograniczTemp(temp[y][x] + 12.0);
                            aktualizujKolorPoRecznejZmianieTemp(x, y);
                        } else if (wybrany == NARZ_MROZENIE) {
                            temp[y][x] = ograniczTemp(temp[y][x] - 12.0);
                            aktualizujKolorPoRecznejZmianieTemp(x, y);
                        } else if (wybrany == OGIEN && siatka[y][x] != SCIANA) {
                            temp[y][x] = ograniczTemp(temp[y][x] + 22.0);
                            aktualizujKolorPoRecznejZmianieTemp(x, y);
                        }
                    }
                }
            }
        }
    }

    private void rysuj(MouseEvent e) {
        if (!wGranicach(myszX, myszY)) return;
        boolean gumka = czyPrawyPrzycisk(e) || wybrany == PUSTE;
        int sx = myszX, sy = myszY, r = pedzel - 1;

        if (!gumka && wybrany == NARZ_PRZESUN) {
            if (popPrzesunX >= 0 && popPrzesunY >= 0) {
                int dxM = sx - popPrzesunX;
                int dyM = sy - popPrzesunY;
                int steps = Math.max(Math.abs(dxM), Math.abs(dyM));
                if (steps > 0) {
                    int stepX = Integer.compare(dxM, 0);
                    int stepY = Integer.compare(dyM, 0);
                    for (int i = 0; i < steps; i++) {
                        int mx = (Math.abs(dxM) > i) ? stepX : 0;
                        int my = (Math.abs(dyM) > i) ? stepY : 0;
                        przesuwajWPedzlu(popPrzesunX + mx * (i + 1), popPrzesunY + my * (i + 1), r, mx, my);
                    }
                }
            }
            popPrzesunX = sx;
            popPrzesunY = sy;
            odswiezEtykieteTemp();
            if (pauza) repaint();
            return;
        }

        if (popRysX >= 0 && popRysY >= 0) {
            int dx = sx - popRysX;
            int dy = sy - popRysY;
            int kroki = Math.max(Math.abs(dx), Math.abs(dy));
            for (int i = 0; i <= kroki; i++) {
                int ix = (kroki == 0) ? sx : popRysX + (dx * i) / kroki;
                int iy = (kroki == 0) ? sy : popRysY + (dy * i) / kroki;
                rysujPunkt(ix, iy, r, gumka);
            }
        } else {
            rysujPunkt(sx, sy, r, gumka);
        }

        popRysX = sx;
        popRysY = sy;
        odswiezEtykieteTemp();
        if (pauza) repaint();
    }

    private boolean wGranicach(int x, int y) {
        return x >= 0 && x < SZER && y >= 0 && y < WYS;
    }

    private boolean wolne(int x, int y) {
        return wGranicach(x, y) && (siatka[y][x] == PUSTE || siatka[y][x] == WODA || siatka[y][x] == OLEJ || czyGaz(siatka[y][x]));
    }

    private boolean czyZsunacPodKatem(int x, int y, int k, int katStopnie) {
        if (!wolne(x + k, y + 1)) return false;

        int wymaganaWysokosc = Math.max(1, (int) Math.ceil(1.0 / Math.tan(Math.toRadians(katStopnie))));
        for (int dy = 1; dy <= wymaganaWysokosc; dy++) {
            if (!wolne(x + k, y + dy)) return false;
        }
        return true;
    }

    private void zamien(int x1, int y1, int x2, int y2) {
        int tt = siatka[y1][x1], tc = czas[y1][x1], tm = maxCzas[y1][x1];
        double ttemp = temp[y1][x1];
        Color tk = kolory[y1][x1];

        siatka[y1][x1] = siatka[y2][x2]; czas[y1][x1] = czas[y2][x2]; maxCzas[y1][x1] = maxCzas[y2][x2];
        temp[y1][x1] = temp[y2][x2]; kolory[y1][x1] = kolory[y2][x2];

        siatka[y2][x2] = tt; czas[y2][x2] = tc; maxCzas[y2][x2] = tm;
        temp[y2][x2] = ttemp; kolory[y2][x2] = tk;
    }

    private void aktualizujTemperaturyIStany() {
        int[] dxS = {0, -1, 1, 0};
        int[] dyS = {1, 0, 0, -1};
        float jasnoscTroll = wspolczynnikJasnosciEkranaTroll();

        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                int t = siatka[y][x];
                if (t == OGIEN) {
                    temp[y][x] = ograniczTemp(temp[y][x] - 10.0);
                    if (temp[y][x] >= 100.0) {
                        kolory[y][x] = kolorOgnia(temp[y][x]);
                    }

                    for (int i = 0; i < 4; i++) {
                        int nx = x + dxS[i], ny = y + dyS[i];
                        if (!wGranicach(nx, ny)) continue;
                        int sas = siatka[ny][nx];
                        if (sas == OLEJ) {
                            if (rand.nextFloat() < 0.14f) {
                                ustawZTemp(nx, ny, OGIEN, Math.max(850.0, temp[y][x]));
                            } else {
                                temp[ny][nx] = ograniczTemp(temp[ny][nx] + 30.0);
                            }
                        } else if (sas != PUSTE && sas != OGIEN && sas != SCIANA) {
                            temp[ny][nx] = ograniczTemp(temp[ny][nx] + 8.0);
                        }
                    }
                } else if (t == OLEJ) {
                    if (temp[y][x] >= 220.0 && rand.nextFloat() < 0.12f) {
                        ustawZTemp(x, y, OGIEN, Math.max(850.0, temp[y][x]));
                    }
                } else if (t == LAWA) {
                    boolean dotknelaWody = false;
                    for (int i = 0; i < 4; i++) {
                        int nx = x + dxS[i], ny = y + dyS[i];
                        if (!wGranicach(nx, ny)) continue;
                        int sas = siatka[ny][nx];
                        if (sas == WODA) {
                            ustawZTemp(nx, ny, PARA, 115.0);
                            dotknelaWody = true;
                        } else if (sas == OLEJ) {
                            if (rand.nextFloat() < 0.22f) {
                                ustawZTemp(nx, ny, OGIEN, 900.0);
                            } else {
                                temp[ny][nx] = ograniczTemp(temp[ny][nx] + 45.0);
                            }
                        } else if (sas == MOKRY_PIASEK) {
                            ustawZTemp(nx, ny, PIASEK, Math.max(55.0, temp[ny][nx] + 25.0));
                        } else if (sas == BLOTO) {
                            int nowyTyp = (rand.nextDouble() < 0.25) ? MUL : ZIEMIA;
                            ustawZTemp(nx, ny, nowyTyp, Math.max(55.0, temp[ny][nx] + 25.0));
                        } else if (sas == PUSTE) {
                            if (temp[y][x] >= 600.0 && rand.nextFloat() < 0.08f) {
                                ustawZTemp(nx, ny, OGIEN, Math.min(950.0, Math.max(650.0, temp[y][x] * 0.75)));
                            }
                        } else if (sas != LAWA && sas != SCIANA && sas != OGIEN) {
                            temp[ny][nx] = ograniczTemp(temp[ny][nx] + 35.0);
                            if (temp[y][x] >= 800.0 && rand.nextFloat() < 0.06f) {
                                ustawZTemp(nx, ny, OGIEN, 850.0);
                            }
                        }
                    }
                    if (dotknelaWody) {
                        int powstal = losujMaterialPoKontakcieZWoda();
                        ustawZTemp(x, y, powstal, Math.min(temp[y][x], 900.0));
                    } else {
                        kolory[y][x] = kolorLawy(temp[y][x]);
                    }
                } else if (t == PLYNNE_ZELAZO || t == PLYNNA_MIEDZ) {
                    for (int i = 0; i < 4; i++) {
                        int nx = x + dxS[i], ny = y + dyS[i];
                        if (!wGranicach(nx, ny)) continue;
                        int sas = siatka[ny][nx];
                        if (sas == WODA) {
                            ustawZTemp(nx, ny, PARA, 115.0);
                            temp[y][x] = ograniczTemp(temp[y][x] - 280.0);
                        } else if (sas != PUSTE && sas != SCIANA && sas != t) {
                            temp[ny][nx] = ograniczTemp(temp[ny][nx] + 25.0);
                        }
                    }
                } else if (t == MIEDZ) {
                    boolean dotykaElektrycznosci = false;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0) continue;
                            int nx = x + dx, ny = y + dy;
                            if (wGranicach(nx, ny) && siatka[ny][nx] == ELEKTRYCZNOSC) {
                                dotykaElektrycznosci = true;
                                break;
                            }
                        }
                        if (dotykaElektrycznosci) break;
                    }
                    if (dotykaElektrycznosci && rand.nextFloat() < 0.05f) {
                        int dir = rand.nextInt(4);
                        int nx = x + dxS[dir], ny = y + dyS[dir];
                        if (wGranicach(nx, ny) && siatka[ny][nx] == PUSTE) {
                            ustawZTemp(nx, ny, ELEKTRYCZNOSC, Math.max(80.0, temp[y][x] + 15.0));
                        }
                    }
                } else if (t == ROSLINA) {
                    for (int i = 0; i < 4; i++) {
                        int nx = x + dxS[i], ny = y + dyS[i];
                        if (wGranicach(nx, ny) && siatka[ny][nx] == CO2) {
                            if (rand.nextFloat() < 0.35f * jasnoscTroll) {
                                ustawZTemp(nx, ny, TLEN, temp[ny][nx]);
                            }
                        }
                    }
                } else if (t == ELEKTRYCZNOSC) {
                    for (int i = 0; i < 4; i++) {
                        int nx = x + dxS[i], ny = y + dyS[i];
                        if (!wGranicach(nx, ny)) continue;
                        int sas = siatka[ny][nx];
                        if (sas == OLEJ && rand.nextFloat() < 0.25f) {
                            ustawZTemp(nx, ny, OGIEN, 900.0);
                        } else if (sas != PUSTE && sas != SCIANA && sas != ELEKTRYCZNOSC) {
                            temp[ny][nx] = ograniczTemp(temp[ny][nx] + 8.0);
                        }
                    }
                }
            }
        }

        double[][] nowaTemp = new double[WYS][SZER];
        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                int t = siatka[y][x];
                if (t == PUSTE) {
                    nowaTemp[y][x] = 0.0;
                    continue;
                }

                if (t == PARA) {
                    nowaTemp[y][x] = ograniczTemp(temp[y][x]);
                    continue;
                }

                double sumaSasiadow = 0.0;
                int liczbaSasiadow = 0;

                for (int i = 0; i < 4; i++) {
                    int nx = x + dxS[i], ny = y + dyS[i];
                    if (wGranicach(nx, ny) && siatka[ny][nx] != PUSTE) {
                        sumaSasiadow += temp[ny][nx];
                        liczbaSasiadow++;
                    }
                }

                if (liczbaSasiadow > 0) {
                    double srednia = (temp[y][x] + sumaSasiadow) / (liczbaSasiadow + 1.0);
                    nowaTemp[y][x] = ograniczTemp(temp[y][x] + (srednia - temp[y][x]) * 0.25);
                } else {
                    nowaTemp[y][x] = ograniczTemp(temp[y][x] + (TEMP_POKOJOWA - temp[y][x]) * 0.05);
                }
            }
        }

        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                temp[y][x] = nowaTemp[y][x];
            }
        }

        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                int t = siatka[y][x];
                if (t == PUSTE || t == SCIANA) continue;
                double aktualnaTemp = temp[y][x];

                if (t == OGIEN) {
                    czas[y][x]--;
                    if (aktualnaTemp < 100.0) {
                        ustawZTemp(x, y, DYM, Math.max(25.0, aktualnaTemp));
                    } else if (czas[y][x] <= 0) {
                        ustawZTemp(x, y, PUSTE, 0.0);
                    } else {
                        kolory[y][x] = kolorOgnia(aktualnaTemp);
                    }
                } else if (t == DYM) {
                    if (czas[y][x] > 0) {
                        czas[y][x]--;
                        if (czas[y][x] <= 0) {
                            ustawZTemp(x, y, PUSTE, 0.0);
                        }
                    } else {
                        ustawZTemp(x, y, DYM, aktualnaTemp);
                    }
                } else if (t == ELEKTRYCZNOSC) {
                    czas[y][x]--;
                    if (czas[y][x] <= 0) {
                        ustawZTemp(x, y, PUSTE, 0.0);
                    } else {
                        kolory[y][x] = kolorElektrycznosci();
                    }
                } else if (t == WODA) {
                    if (aktualnaTemp >= 100.0) {
                        ustawZTemp(x, y, PARA, Math.max(115.0, aktualnaTemp));
                    } else if (aktualnaTemp <= -1.0) {
                        ustawZTemp(x, y, LOD, aktualnaTemp);
                    }
                } else if (t == PARA) {
                    if (aktualnaTemp < 100.0) {
                        ustawZTemp(x, y, WODA, aktualnaTemp);
                    }
                } else if (t == BLOTO) {
                    if (aktualnaTemp <= -20.0) {
                        ustawZTemp(x, y, ZMARZLINA, aktualnaTemp);
                    } else if (aktualnaTemp >= 50.0) {
                        int nowyTyp = (rand.nextDouble() < 0.25) ? MUL : ZIEMIA;
                        ustawZTemp(x, y, nowyTyp, aktualnaTemp);
                    }
                } else if (t == MOKRY_PIASEK) {
                    if (aktualnaTemp >= 50.0) {
                        if (rand.nextDouble() < 0.09) {
                            ustawZTemp(x, y, PIASKOWIEC, aktualnaTemp);
                        } else {
                            ustawZTemp(x, y, PIASEK, aktualnaTemp);
                        }
                    }
                } else if (t == LOD) {
                    if (aktualnaTemp >= 0.0) {
                        ustawZTemp(x, y, WODA, aktualnaTemp);
                    }
                } else if (t == ZMARZLINA) {
                    if (aktualnaTemp >= 0.0) {
                        ustawZTemp(x, y, BLOTO, aktualnaTemp);
                    }
                } else if (t == BAZALT) {
                    if (aktualnaTemp >= 1100.0) {
                        ustawZTemp(x, y, LAWA, aktualnaTemp);
                    }
                } else if (t == OBSYDIAN) {
                    if (aktualnaTemp >= 1000.0) {
                        ustawZTemp(x, y, LAWA, aktualnaTemp);
                    }
                } else if (t == KAMIEN) {
                    if (aktualnaTemp >= 1000.0) {
                        ustawZTemp(x, y, LAWA, aktualnaTemp);
                    }
                } else if (t == ZELAZO) {
                    if (aktualnaTemp >= 1580.0) {
                        ustawZTemp(x, y, PLYNNE_ZELAZO, aktualnaTemp);
                    }
                } else if (t == PLYNNE_ZELAZO) {
                    if (aktualnaTemp < 1580.0) {
                        ustawZTemp(x, y, ZELAZO, aktualnaTemp);
                    } else {
                        kolory[y][x] = kolorPlynnegoZelaza(aktualnaTemp);
                    }
                } else if (t == MIEDZ) {
                    if (aktualnaTemp >= 1080.0) {
                        ustawZTemp(x, y, PLYNNA_MIEDZ, aktualnaTemp);
                    }
                } else if (t == PLYNNA_MIEDZ) {
                    if (aktualnaTemp < 1080.0) {
                        ustawZTemp(x, y, MIEDZ, aktualnaTemp);
                    } else {
                        kolory[y][x] = kolorPlynnejMiedzi(aktualnaTemp);
                    }
                } else if (t == LAWA) {
                    boolean obokWody = false;
                    for (int i = 0; i < 4; i++) {
                        int nx = x + dxS[i], ny = y + dyS[i];
                        if (wGranicach(nx, ny) && siatka[ny][nx] == WODA) {
                            ustawZTemp(nx, ny, PARA, 115.0);
                            obokWody = true;
                        }
                    }
                    if (obokWody) {
                        int powstal = losujMaterialPoKontakcieZWoda();
                        ustawZTemp(x, y, powstal, Math.min(aktualnaTemp, 900.0));
                    } else if (aktualnaTemp < 1000.0) {
                        int powstal = losujMaterialPoStygnięciuLawy();
                        ustawZTemp(x, y, powstal, aktualnaTemp);
                    } else {
                        kolory[y][x] = kolorLawy(aktualnaTemp);
                    }
                }
            }
        }
    }

    private void tick() {
        licznikTickow++;
        aktualizujTemperaturyIStany();

        if (pauza) {
            return;
        }

        boolean[][] akt = new boolean[WYS][SZER];
        int[] dxS = {0, -1, 1, 0};
        int[] dyS = {1, 0, 0, -1};
        List<Point> magnesy = znajdzMagnesy();

        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                if (siatka[y][x] != PIASEK) continue;

                for (int i = 0; i < 4; i++) {
                    int nx = x + dxS[i], ny = y + dyS[i];
                    if (wGranicach(nx, ny) && siatka[ny][nx] == BLOTO) {
                        if (rand.nextDouble() < 0.07) {
                            int celX = rand.nextBoolean() ? x : nx;
                            int celY = (celX == x) ? y : ny;
                            int nowyTyp = rand.nextBoolean() ? ZIEMIA : MOKRY_PIASEK;
                            ustawZTemp(celX, celY, nowyTyp, temp[celY][celX]);
                        }
                        break;
                    }
                }
            }
        }

        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                if (siatka[y][x] != KWAS) continue;

                for (int i = 0; i < 4; i++) {
                    if (siatka[y][x] != KWAS) break;
                    int nx = x + dxS[i], ny = y + dyS[i];
                    if (wGranicach(nx, ny)) {
                        int cel = siatka[ny][nx];
                        if (cel != PUSTE && cel != KWAS && cel != SCIANA) {
                            ustawZTemp(nx, ny, PUSTE, 0.0);
                            ustawZTemp(x, y, PUSTE, 0.0);
                            akt[ny][nx] = true;
                            akt[y][x] = true;
                        }
                    }
                }
            }
        }

        for (int y = WYS - 1; y >= 0; y--) {
            for (int x = 0; x < SZER; x++) {
                if (siatka[y][x] != TNT || akt[y][x]) continue;
                if (czyTntPowinienWybuchnac(x, y)) {
                    wybuchTNT(x, y);
                }
            }
        }

        for (int y = WYS - 1; y >= 0; y--) {
            for (int x = 0; x < SZER; x++) {
                if (siatka[y][x] != BOMBA || akt[y][x]) continue;
                if (bombaDotykaCzegos(x, y)) {
                    wybuchBomby(x, y);
                }
            }
        }

        for (int y = WYS - 1; y >= 0; y--) {
            for (int x = 0; x < SZER; x++) {
                if (siatka[y][x] != ANTY_BOMBA || akt[y][x]) continue;
                if (bombaDotykaCzegos(x, y)) {
                    wybuchAntyBomby(x, y, PROMIEN_WYBUCHU_BOMBY);
                }
            }
        }

        for (int y = WYS - 1; y >= 0; y--) {
            for (int x = 0; x < SZER; x++) {
                int t = siatka[y][x];
                if (akt[y][x] || t == PUSTE || t == SCIANA || t == PIASKOWIEC || t == BAZALT || t == TNT || t == MAGNES || t == ROSLINA || czyPrzyciaganePrzezMagnes(t) || t == LOD || t == OGIEN || czyGaz(t)) continue;
                int k = rand.nextBoolean() ? -1 : 1;

                if (t == BOMBA) {
                    if (y + 1 < WYS && siatka[y + 1][x] == PUSTE) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                        if (bombaDotykaCzegos(x, y + 1)) {
                            wybuchBomby(x, y + 1);
                        }
                    } else {
                        wybuchBomby(x, y);
                    }
                } else if (t == ANTY_BOMBA) {
                    if (y + 1 < WYS && siatka[y + 1][x] == PUSTE) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                        if (bombaDotykaCzegos(x, y + 1)) {
                            wybuchAntyBomby(x, y + 1, PROMIEN_WYBUCHU_BOMBY);
                        }
                    } else {
                        wybuchAntyBomby(x, y, PROMIEN_WYBUCHU_BOMBY);
                    }
                } else if (t == ELEKTRYCZNOSC) {
                    if (rand.nextFloat() < 0.25f) {
                        int dx = rand.nextInt(3) - 1;
                        int dy = rand.nextInt(3) - 1;
                        int nx = x + dx, ny = y + dy;
                        if (wGranicach(nx, ny) && siatka[ny][nx] == PUSTE) {
                            zamien(x, y, nx, ny);
                            akt[ny][nx] = true;
                        }
                    }
                } else if ((t == PIASEK || t == MOKRY_PIASEK || t == ZIEMIA || t == ZMARZLINA || t == KAMIEN || t == MUL || t == OBSYDIAN) && y + 1 < WYS) {
                    if (siatka[y + 1][x] == WODA && rand.nextFloat() > 0.33f) continue;
                    if (wolne(x, y + 1)) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                    } else if (t == PIASEK) {
                        if (czyZsunacPodKatem(x, y, k, 45)) {
                            zamien(x, y, x + k, y + 1);
                            akt[y + 1][x + k] = true;
                        }
                    } else if (t == OBSYDIAN) {
                        if (czyZsunacPodKatem(x, y, k, 40)) {
                            zamien(x, y, x + k, y + 1);
                            akt[y + 1][x + k] = true;
                        }
                    } else if (t == MOKRY_PIASEK || t == ZIEMIA) {
                        if (czyZsunacPodKatem(x, y, k, 30)) {
                            zamien(x, y, x + k, y + 1);
                            akt[y + 1][x + k] = true;
                        }
                    } else if (t == ZMARZLINA || t == MUL) {
                        if (czyZsunacPodKatem(x, y, k, 20)) {
                            zamien(x, y, x + k, y + 1);
                            akt[y + 1][x + k] = true;
                        }
                    } else if (t == KAMIEN) {
                        if (czyZsunacPodKatem(x, y, k, 15)) {
                            zamien(x, y, x + k, y + 1);
                            akt[y + 1][x + k] = true;
                        }
                    }
                } else if (t == BLOTO && y + 1 < WYS) {
                    if (wolne(x, y + 1)) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                    } else if (wolne(x + k, y + 1)) {
                        zamien(x, y, x + k, y + 1);
                        akt[y + 1][x + k] = true;
                    } else if (rand.nextFloat() < 0.15f && wolne(x + k, y)) {
                        zamien(x, y, x + k, y);
                        akt[y][x + k] = true;
                    }
                } else if (t == WODA) {
                    boolean wsiakla = false;
                    for (int i = 0; i < 4; i++) {
                        int nx = x + dxS[i], ny = y + dyS[i];
                        if (wGranicach(nx, ny) && (siatka[ny][nx] == PIASEK || siatka[ny][nx] == ZIEMIA)) {
                            ustawZTemp(nx, ny, siatka[ny][nx] == PIASEK ? MOKRY_PIASEK : BLOTO, temp[ny][nx]);
                            ustawZTemp(x, y, PUSTE, 0.0);
                            wsiakla = true;
                            break;
                        }
                    }
                    if (wsiakla) continue;

                    if (y + 1 < WYS && (siatka[y + 1][x] == PUSTE || siatka[y + 1][x] == OLEJ || czyGaz(siatka[y + 1][x]))) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                    } else if (wGranicach(x + k, y) && (siatka[y][x + k] == PUSTE || siatka[y][x + k] == OLEJ || czyGaz(siatka[y][x + k]))) {
                        zamien(x, y, x + k, y);
                        akt[y][x + k] = true;
                    }
                } else if (t == OLEJ) {
                    if (y + 1 < WYS && (siatka[y + 1][x] == PUSTE || czyGaz(siatka[y + 1][x]))) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                    } else if (wGranicach(x + k, y) && (siatka[y][x + k] == PUSTE || czyGaz(siatka[y][x + k]))) {
                        zamien(x, y, x + k, y);
                        akt[y][x + k] = true;
                    }
                } else if (t == KWAS) {
                    if (y + 1 < WYS && (siatka[y + 1][x] == PUSTE || czyGaz(siatka[y + 1][x]))) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                    } else if (wGranicach(x + k, y) && (siatka[y][x + k] == PUSTE || czyGaz(siatka[y][x + k]))) {
                        zamien(x, y, x + k, y);
                        akt[y][x + k] = true;
                    }
                } else if (t == LAWA || t == PLYNNE_ZELAZO || t == PLYNNA_MIEDZ) {
                    if (y + 1 < WYS && (siatka[y + 1][x] == PUSTE || siatka[y + 1][x] == OLEJ || czyGaz(siatka[y + 1][x]))) {
                        zamien(x, y, x, y + 1);
                        akt[y + 1][x] = true;
                    } else if (y + 1 < WYS && wolne(x + k, y + 1) && siatka[y + 1][x + k] != WODA) {
                        zamien(x, y, x + k, y + 1);
                        akt[y + 1][x + k] = true;
                    } else if (wGranicach(x + k, y) && (siatka[y][x + k] == PUSTE || siatka[y][x + k] == OLEJ || czyGaz(siatka[y][x + k]))) {
                        zamien(x, y, x + k, y);
                        akt[y][x + k] = true;
                    }
                }
            }
        }

        if (!magnesy.isEmpty()) {
            for (int y = WYS - 1; y >= 0; y--) {
                for (int x = 0; x < SZER; x++) {
                    if (czyPrzyciaganePrzezMagnes(siatka[y][x]) && !akt[y][x]) {
                        przyciagnijMetalDoMagnesu(x, y, magnesy, akt);
                    }
                }
            }
        } else {
            for (int y = 0; y < WYS; y++) {
                for (int x = 0; x < SZER; x++) {
                    if (czyPrzyciaganePrzezMagnes(siatka[y][x])) {
                        czas[y][x] = 0;
                    }
                }
            }
        }

        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                int t = siatka[y][x];
                if (akt[y][x] || (t != OGIEN && !czyGaz(t))) continue;

                if (t == OGIEN) {
                    int dx = rand.nextInt(3) - 1;
                    if (y > 0 && rand.nextFloat() < 0.65f && wGranicach(x + dx, y - 1) && siatka[y - 1][x + dx] == PUSTE) {
                        zamien(x, y, x + dx, y - 1);
                        akt[y - 1][x + dx] = true;
                    }
                } else if (czyGazLosowy(t)) {
                    int dx = rand.nextInt(3) - 1;
                    int dy = rand.nextInt(3) - 1;
                    int nx = x + dx, ny = y + dy;
                    if ((nx != x || ny != y) && wGranicach(nx, ny) && siatka[ny][nx] == PUSTE) {
                        zamien(x, y, nx, ny);
                        akt[ny][nx] = true;
                    }
                } else {
                    int dx = rand.nextInt(3) - 1, bok = rand.nextBoolean() ? -1 : 1;
                    if (y > 0 && wGranicach(x + dx, y - 1) && siatka[y - 1][x + dx] == PUSTE) {
                        zamien(x, y, x + dx, y - 1);
                        akt[y - 1][x + dx] = true;
                    } else if (wGranicach(x + bok, y) && siatka[y][x + bok] == PUSTE) {
                        zamien(x, y, x + bok, y);
                        akt[y][x + bok] = true;
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Composite staryComposite = g2.getComposite();

        for (int y = 0; y < WYS; y++) {
            for (int x = 0; x < SZER; x++) {
                int t = siatka[y][x];
                if (t == PUSTE || kolory[y][x] == null) continue;

                if (przezroczysteGazyR && czyGaz(t)) {
                    float alpha = alphaPoOdleglosciIRozmiarzeGrupy(x, y, t);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                } else {
                    g2.setComposite(staryComposite);
                }

                g2.setColor(kolory[y][x]);
                g2.fillRect(x * SKALA, y * SKALA, SKALA, SKALA);
            }
        }

        g2.setComposite(staryComposite);
    }

    public void actionPerformed(ActionEvent e) {
        long terazNano = System.nanoTime();
        long uplyneloNano = Math.max(0L, terazNano - ostatniCzasNano);
        ostatniCzasNano = terazNano;

        akumulatorNano += uplyneloNano;

        int wykonaneTicki = 0;
        while (akumulatorNano >= NANOSEKUND_NA_TICK && wykonaneTicki < 5) {
            tick();
            akumulatorNano -= NANOSEKUND_NA_TICK;
            wykonaneTicki++;
        }

        if (wykonaneTicki > 0) {
            odswiezEtykieteTemp();
            repaint();
        }
    }

    private static JButton przyciskNarzedzia(String nazwa, Color tlo, Color tekst, int idNarzedzia, Sandboxia gra) {
        JButton b = new JButton(styl(nazwa));
        b.setBackground(tlo);
        b.setForeground(tekst);
        b.setFont(CZCIONKA);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setMargin(new Insets(3, 6, 3, 6));
        b.addActionListener(e -> {
            gra.wybrany = idNarzedzia;
            gra.requestFocusInWindow();
        });
        return b;
    }

    private JButton przyciskElementu(int id) {
        Color tlo = BAZY[id];
        double jasnosc = 0.299 * tlo.getRed() + 0.587 * tlo.getGreen() + 0.114 * tlo.getBlue();
        Color tekst = jasnosc > 140 ? Color.BLACK : Color.WHITE;
        JButton b = przyciskNarzedzia(tNazwa(id), tlo, tekst, id, this);
        przyciskiElementow.put(id, b);
        return b;
    }

    private static JPanel panelElementow(Sandboxia gra, int... ids) {
        JPanel p = new JPanel(new GridLayout(0, 5, 3, 3));
        p.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        for (int id : ids) {
            p.add(gra.przyciskElementu(id));
        }
        return p;
    }

    public static void main(String[] args) {
        UIManager.put("TabbedPane.tabInsets", new Insets(3, 6, 3, 6));
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        JFrame okno = new JFrame("Sandboxia");
        okno.setResizable(false);
        okno.getContentPane().setLayout(new BorderLayout(0, 0));
        Sandboxia gra = new Sandboxia();

        JPanel panelTemperatury = new JPanel(new BorderLayout(0, 0));
        panelTemperatury.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        gra.etykietaTemp = new JLabel(styl(gra.tNajedzTemp() + gra.dopisekTrybuR()));
        gra.etykietaTemp.setFont(CZCIONKA);
        gra.etykietaTemp.setBorder(BorderFactory.createEmptyBorder(3, 4, 2, 4));

        JPanel panelTermoPrzyciski = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        panelTermoPrzyciski.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gra.btnOcieplanie = przyciskNarzedzia(gra.tOcieplanie(), Color.decode("#E65100"), Color.WHITE, NARZ_OCIEPLANIE, gra);
        gra.btnMrozenie = przyciskNarzedzia(gra.tMrozenie(), Color.decode("#0277BD"), Color.WHITE, NARZ_MROZENIE, gra);
        gra.btnMieszanie = przyciskNarzedzia(gra.tMieszanie(), Color.decode("#FBC02D"), Color.BLACK, NARZ_MIESZANIE, gra);
        gra.btnPrzesun = przyciskNarzedzia(gra.tPrzesun(), Color.decode("#546E7A"), Color.WHITE, NARZ_PRZESUN, gra);

        gra.btnUstawienia = new JButton(styl("⚙"));
        gra.btnUstawienia.setFont(CZCIONKA);
        gra.btnUstawienia.setFocusPainted(false);
        gra.btnUstawienia.setMargin(new Insets(3, 6, 3, 6));
        gra.btnUstawienia.addActionListener(e -> gra.otworzOknoUstawien());

        panelTermoPrzyciski.add(gra.btnOcieplanie);
        panelTermoPrzyciski.add(gra.btnMrozenie);
        panelTermoPrzyciski.add(gra.btnMieszanie);
        panelTermoPrzyciski.add(gra.btnPrzesun);
        panelTermoPrzyciski.add(gra.btnUstawienia);

        panelTemperatury.add(gra.etykietaTemp, BorderLayout.NORTH);
        panelTemperatury.add(panelTermoPrzyciski, BorderLayout.SOUTH);

        gra.zakladki = new JTabbedPane();
        gra.zakladki.setFont(CZCIONKA);
        gra.zakladki.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel nat = panelElementow(gra, PIASEK, MOKRY_PIASEK, PIASKOWIEC, ZIEMIA, BLOTO, MUL, ZMARZLINA, KAMIEN, BAZALT);
        JPanel ply = panelElementow(gra, WODA, OLEJ, KWAS, LAWA, PLYNNE_ZELAZO, PLYNNA_MIEDZ);
        JPanel zyc = panelElementow(gra, ROSLINA);
        JPanel sol = panelElementow(gra, SCIANA, LOD);
        JPanel gaz = panelElementow(gra, PARA, DYM, TLEN, CO2);
        JPanel ene = panelElementow(gra, OGIEN, ELEKTRYCZNOSC);
        JPanel bro = panelElementow(gra, BOMBA, TNT, ANTY_BOMBA);
        JPanel min = panelElementow(gra, OBSYDIAN, ZELAZO, MIEDZ, MAGNES);

        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][0]), nat);
        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][1]), ply);
        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][2]), zyc);
        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][3]), sol);
        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][4]), gaz);
        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][5]), ene);
        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][6]), bro);
        gra.zakladki.addTab(styl(ZAKLADKI_JEZYKI[0][7]), min);

        int wysZakladek = Math.max(118, gra.zakladki.getPreferredSize().height);
        gra.zakladki.setPreferredSize(new Dimension(SZER_EKRANU, wysZakladek));
        gra.zakladki.setMaximumSize(new Dimension(SZER_EKRANU, wysZakladek));

        JPanel narz = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        narz.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gra.etykietaPedzla = new JLabel(styl(gra.tPedzel() + "3"));
        gra.etykietaPedzla.setFont(CZCIONKA);
        gra.suwakPedzla = new JSlider(1, 15, 3);
        gra.suwakPedzla.setPreferredSize(new Dimension(110, 22));
        gra.suwakPedzla.addChangeListener(e -> {
            gra.pedzel = gra.suwakPedzla.getValue();
            gra.etykietaPedzla.setText(styl(gra.tPedzel() + gra.pedzel));
            gra.requestFocusInWindow();
        });

        gra.btnGumka = przyciskNarzedzia(gra.tGumka(), BAZY[PUSTE], Color.WHITE, PUSTE, gra);
        narz.add(gra.btnGumka);
        narz.add(gra.etykietaPedzla);
        narz.add(gra.suwakPedzla);

        JPanel dol = new JPanel(new BorderLayout(0, 0));
        dol.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dol.add(panelTemperatury, BorderLayout.NORTH);
        JPanel dolSrodek = new JPanel(new BorderLayout(0, 0));
        dolSrodek.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dolSrodek.add(narz, BorderLayout.NORTH);
        dolSrodek.add(gra.zakladki, BorderLayout.SOUTH);
        dol.add(dolSrodek, BorderLayout.SOUTH);
        dol.setPreferredSize(new Dimension(SZER_EKRANU, dol.getPreferredSize().height));

        okno.add(gra, BorderLayout.CENTER);
        okno.add(dol, BorderLayout.SOUTH);
        okno.pack();
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setLocationRelativeTo(null);
        okno.setVisible(true);
        gra.requestFocusInWindow();
    }
}