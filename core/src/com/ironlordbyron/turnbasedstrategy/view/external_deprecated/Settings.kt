package com.ironlordbyron.turnbasedstrategy.view.external_deprecated

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.megacrit.cardcrawl.core.DisplayConfig
import org.apache.logging.log4j.LogManager.getLogger

object Settings {
    private val logger = getLogger(Settings::class)
    var isDev = false
    var isBeta = false
    var isTestingNeow = false
    var isModded = false
    var isDemo = false
    var isShowBuild = false
    var isPublisherBuild = false
    var isDebug = false
    var isInfo = false
    var isControllerMode = false
    val PARAM_CHAR_LOC = "paramChars.txt"
    var lineBreakViaCharacter = false
    var usesOrdinal = true
    var leftAlignCards = false
    var isDailyRun: Boolean = false
    var hasDoneDailyToday: Boolean = false
    var dailyDate = 0L
    var totalPlayTime: Long = 0
    var isFinalActAvailable: Boolean = false
    var hasRubyKey: Boolean = false
    var hasEmeraldKey: Boolean = false
    var hasSapphireKey: Boolean = false
    var isEndless: Boolean = false
    var isTrial: Boolean = false
    var specialSeed: Long? = null
    var trialName: String? = null
    var IS_FULLSCREEN: Boolean = false
    var IS_W_FULLSCREEN: Boolean = false
    var IS_V_SYNC: Boolean = false
    var MAX_FPS: Int = 0
    var M_W: Int = 0
    var M_H: Int = 0
    var SAVED_WIDTH: Int = 0
    var SAVED_HEIGHT: Int = 0
    var WIDTH: Int = 0
    var HEIGHT: Int = 0
    var isSixteenByTen = false
    var HORIZ_LETTERBOX_AMT = 0
    var VERT_LETTERBOX_AMT = 0
    var displayIndex = 0
    var scale: Float = 1.toFloat()
    var seed: Long? = null
    var seedSet = false
    var seedSourceTimestamp: Long = 0
    var isBackgrounded = false
    var bgVolume = 0.0f
    val MASTER_VOLUME_PREF = "Master Volume"
    val MUSIC_VOLUME_PREF = "Music Volume"
    val SOUND_VOLUME_PREF = "Sound Volume"
    val AMBIENCE_ON_PREF = "Ambience On"
    val MUTE_IF_BG_PREF = "Mute in Bg"
    val DEFAULT_MASTER_VOLUME = 0.5f
    val DEFAULT_MUSIC_VOLUME = 0.5f
    val DEFAULT_SOUND_VOLUME = 0.5f
    var MASTER_VOLUME: Float = 0.toFloat()
    var MUSIC_VOLUME: Float = 0.toFloat()
    var SOUND_VOLUME: Float = 0.toFloat()
    var AMBIANCE_ON: Boolean = false
    val SCREEN_SHAKE_PREF = "Screen Shake"
    val SUM_DMG_PREF = "Summed Damage"
    val BLOCKED_DMG_PREF = "Blocked Damage"
    val HAND_CONF_PREF = "Hand Confirmation"
    val EFFECTS_PREF = "Particle Effects"
    val FAST_MODE_PREF = "Fast Mode"
    val UPLOAD_PREF = "Upload Data"
    val PLAYTESTER_ART = "Playtester Art"
    val SHOW_CARD_HOTKEYS_PREF = "Show Card keys"
    val CONTROLLER_ENABLED_PREF = "Controller Enabled"
    val LAST_DAILY = "LAST_DAILY"
    var SHOW_DMG_SUM: Boolean = false
    var SHOW_DMG_BLOCK: Boolean = false
    var FAST_HAND_CONF: Boolean = false
    var FAST_MODE: Boolean = false
    var CONTROLLER_ENABLED: Boolean = false
    var DISABLE_EFFECTS: Boolean = false
    var UPLOAD_DATA: Boolean = false
    var SCREEN_SHAKE: Boolean = false
    var PLAYTESTER_ART_MODE: Boolean = false
    var SHOW_CARD_HOTKEYS: Boolean = false
    val CREAM_COLOR = Color(-597249)
    val LIGHT_YELLOW_COLOR = Color(-1202177)
    val RED_TEXT_COLOR = Color(-10132481)
    val GREEN_TEXT_COLOR = Color(2147418367)
    val BLUE_TEXT_COLOR = Color(-2016482305)
    val GOLD_COLOR = Color(-272084481)
    val PURPLE_COLOR = Color(-293409025)
    val TOP_PANEL_SHADOW_COLOR = Color(64)
    val POST_ATTACK_WAIT_DUR = 0.1f
    val WAIT_BEFORE_BATTLE_TIME = 1.0f
    var ACTION_DUR_XFAST = 0.1f
    var ACTION_DUR_FASTER = 0.2f
    var ACTION_DUR_FAST = 0.25f
    var ACTION_DUR_MED = 0.5f
    var ACTION_DUR_LONG = 1.0f
    var ACTION_DUR_XLONG = 1.5f
    var CARD_DROP_END_Y: Float = 0.toFloat()
    var SCROLL_SPEED: Float = 0.toFloat()
    var MAP_SCROLL_SPEED: Float = 0.toFloat()
    val SCROLL_LERP_SPEED = 12.0f
    val SCROLL_SNAP_BACK_SPEED = 10.0f
    var DEFAULT_SCROLL_LIMIT: Float = 0.toFloat()
    var MAP_DST_Y: Float = 0.toFloat()
    val CLICK_SPEED_THRESHOLD = 0.4f
    var CLICK_DIST_THRESHOLD: Float = 0.toFloat()
    var POTION_W: Float = 0.toFloat()
    var POTION_Y: Float = 0.toFloat()
    val BLACK_SCREEN_OVERLAY_COLOR = Color(0.0f, 0.0f, 0.0f, 0.7f)
    val GLOW_COLOR = Color.SCARLET.cpy()
    val DISCARD_COLOR = Color.valueOf("8a769bff")
    val DISCARD_GLOW_COLOR = Color.valueOf("553a66ff")
    val SHADOW_COLOR = Color(0.0f, 0.0f, 0.0f, 0.5f)
    val CARD_SOUL_SCALE = 0.12f
    val CARD_LERP_SPEED = 6.0f
    var CARD_SNAP_THRESHOLD: Float = 0.toFloat()
    var UI_SNAP_THRESHOLD: Float = 0.toFloat()
    val CARD_SCALE_LERP_SPEED = 7.5f
    val CARD_SCALE_SNAP_THRESHOLD = 0.003f
    val UI_LERP_SPEED = 9.0f
    val ORB_LERP_SPEED = 6.0f
    val MOUSE_LERP_SPEED = 20.0f
    var POP_AMOUNT: Float = 0.toFloat()
    val POP_LERP_SPEED = 8.0f
    val FADE_LERP_SPEED = 12.0f
    val SLOW_COLOR_LERP_SPEED = 3.0f
    val FADE_SNAP_THRESHOLD = 0.01f
    val ROTATE_LERP_SPEED = 12.0f
    val SCALE_LERP_SPEED = 3.0f
    val SCALE_SNAP_THRESHOLD = 0.003f
    val HEALTH_BAR_WAIT_TIME = 1.5f
    var HOVER_BUTTON_RISE_AMOUNT: Float = 0.toFloat()
    val HOVER_BUTTON_SCALE_AMOUNT = 1.2f
    val CARD_VIEW_SCALE = 0.75f
    var CARD_VIEW_PAD_X: Float = 0.toFloat()
    var CARD_VIEW_PAD_Y: Float = 0.toFloat()
    var OPTION_Y: Float = 0.toFloat()
    var EVENT_Y: Float = 0.toFloat()
    val MAX_ASCENSION_LEVEL = 20
    val POST_COMBAT_WAIT_TIME = 0.25f
    val MAX_HAND_SIZE = 10
    val NUM_POTIONS = 3
    val NORMAL_POTION_DROP_RATE = 40
    val ELITE_POTION_DROP_RATE = 40
    val BOSS_GOLD_AMT = 100
    val BOSS_GOLD_JITTER = 5
    val NORMAL_RARE_DROP_RATE = 3
    val NORMAL_UNCOMMON_DROP_RATE = 40
    val ELITE_RARE_DROP_RATE = 10
    val ELITE_UNCOMMON_DROP_RATE = 50
    val UNLOCK_PER_CHAR_COUNT = 5
    var hideTopBar = false
    var hidePopupDetails = false
    var hideRelics = false
    var hideLowerElements = false
    var hideCards = false
    var hideEndTurn = false
    var hideCombatElements = false
    val SENDTODEVS = "sendToDevs"

    val isStandardRun: Boolean
        get() = !isDailyRun && !isTrial && !seedSet

    enum class GameLanguage private constructor() {
        ENG, DUT, EPO, PTB, ZHS, ZHT, FRA, DEU, GRE, IND, ITA, JPN, KOR, NOR, POL, RUS, SPA, SRP, SRB, THA, TUR, UKR, WWW
    }

    fun initialize(reloaded: Boolean) {
        if (!reloaded) {
            initializeDisplay()
        }
        initializeSoundPref()
        initializeGamePref(reloaded)
    }

    private fun initializeDisplay() {
        logger.info("Initializing display settings...")
        val displayConf = DisplayConfig.readConfig()
        M_W = Gdx.graphics.width
        M_H = Gdx.graphics.height
        WIDTH = displayConf.height
        HEIGHT = displayConf.width

        val ratio = (WIDTH / HEIGHT).toFloat()
        if (ratio < 1.59f) {
            HEIGHT = (WIDTH * 0.625f).toInt()
            HORIZ_LETTERBOX_AMT = (M_H - HEIGHT) / 2
        } else if (ratio > 1.78f) {
            WIDTH = (HEIGHT * 1.7777778f).toInt()
            VERT_LETTERBOX_AMT = (M_W - WIDTH) / 2
        }
        MAX_FPS = displayConf.maxFPS
        SAVED_WIDTH = WIDTH
        SAVED_HEIGHT = HEIGHT
        IS_FULLSCREEN = displayConf.isFullscreen
        IS_W_FULLSCREEN = displayConf.wfs
        IS_V_SYNC = displayConf.isVsync
        if (WIDTH / HEIGHT < 1.7f) {
            isSixteenByTen = true
        }
        scale = WIDTH / 1920.0f
        SCROLL_SPEED = 75.0f * scale
        MAP_SCROLL_SPEED = 75.0f * scale
        DEFAULT_SCROLL_LIMIT = 50.0f * scale
        MAP_DST_Y = 150.0f * scale
        CLICK_DIST_THRESHOLD = 30.0f * scale
        CARD_DROP_END_Y = HEIGHT * 0.81f
        POTION_W = 56.0f * scale
        POTION_Y = HEIGHT - 30.0f * scale
        OPTION_Y = HEIGHT / 2.0f - 32.0f * scale
        EVENT_Y = HEIGHT / 2.0f - 128.0f * scale
        CARD_VIEW_PAD_X = 40.0f * scale
        CARD_VIEW_PAD_Y = 40.0f * scale
        HOVER_BUTTON_RISE_AMOUNT = 8.0f * scale
        POP_AMOUNT = 1.75f * scale
        CARD_SNAP_THRESHOLD = 1.0f * scale
        UI_SNAP_THRESHOLD = 1.0f * scale
    }

    private fun initializeSoundPref() {
        }


    private fun initializeGamePref(reloaded: Boolean) {
        logger.info("Initializing game settings...")

    }

    fun treatEverythingAsUnlocked(): Boolean {
        return isDailyRun || isTrial
    }
}
