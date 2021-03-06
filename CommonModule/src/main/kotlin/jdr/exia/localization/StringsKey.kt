package jdr.exia.localization

// Values whose keys are prefixed by STR are plain strings that should be formated.
// Those prefixed by ST are sentences already formated

// -- Strings

const val STR_VERSION = "version"
const val STR_FILES = "files"
const val STR_TAKE_SCREENSHOT = "take_screenshot"
const val STR_SAVE_AS = "save_as"
const val STR_EXPORT_DATA = "export_data"
const val STR_IMPORT_DATA = "import_data"
const val STR_AUTO_UPDATE = "auto_update"
const val STR_OLEBO_FILE = "olebo_file"
const val STR_ABOUT = "about"
const val STR_APP_VERSION = "app_version"
const val STR_DATABASE_VERSION = "dtb_version"
const val STR_ELEMENTS = "element"
const val STR_ADD_ACT = "add_act"
const val STR_OBJECT_LIST = "objects_list"
const val STR_OBJECTS = "objects"
const val STR_PC = "pc"
const val STR_NPC = "npc"
const val STR_NEW_ACT = "new_act"
const val STR_NAME = "name"
const val STR_CONFIRM = "confirm"
const val STR_NEW_SCENE = "new_scene"
const val STR_CHANGE_SCENE = "change_scene"
const val STR_NAME_OF_SCENE = "name_scene"
const val STR_IMPORT_IMG = "import_img"
const val STR_CANCEL = "cancel"
const val STR_SCENES = "scenes"
const val STR_NEW_ELEMENT = "new_element"
const val STR_CHANGE_ELEMENT = "change_element"
const val STR_NAME_OF_ELEMENT = "name_element"
const val STR_MAX_HEALTH = "hp_max"
const val STR_MAX_MANA = "mp_max"
const val STR_SEARCH = "search"
const val STR_BASE_ELEMENT_PLR = "base_element_plr"
const val STR_BASE_ELEMENT = "base_element"
const val STR_NO_ELEMENT = "no_element"
const val STR_DM_TITLE_FRAME = "master_frame_title"
const val STR_PLAYER_TITLE_FRAME = "player_frame_title"
const val STR_TOOLS = "tools"
const val STR_ENABLE_CURSOR = "enable_cursor"
const val STR_RESTORE = "restore"
const val STR_WINDOW = "window"
const val STR_CLOSE_ACT = "close_act"
const val STR_TOGGLE_PLAYER_FRAME = "toggle_player_frame"
const val STR_CHOOSE_SCENE = "choose_scene"
const val STR_IS_CURRENT_SCENE = "current_scene"
const val STR_TOKENS = "tokens"
const val STR_MANAGE_BLUEPRINTS = "manage_blueprints"
const val STR_IMPORT_FROM_SCENE = "import_from_scene"
const val STR_IMPORT_ALL_ELEMENTS = "import_all_elements"
const val STR_DELETE_SELECTED_TOKENS = "delete_selected"
const val STR_CLEAR_BOARD = "clear_board"
const val STR_DELETION = "delete_title"
const val STR_ROTATE_TO_RIGHT = "rotate_right"
const val STR_ROTATE_TO_LEFT = "rotate_left"
const val STR_VISIBILITY = "visibility"
const val STR_HIDE = "hide"
const val STR_SHOW = "show"
const val STR_DELETE = "delete"
const val STR_DEFAULT = "default"
const val STR_FOREGROUND = "foreground"
const val STR_BACKGROUND = "background"
const val STR_SELECTED_ELEMENTS = "elements_selected"
const val STR_HP = "hp"
const val STR_MP = "MP"
const val STR_IMG = "img"
const val STR_SMALL_IMG = "s_img"
const val STR_WARNING = "warning"
const val STR_CRITICAL_ERROR = "critical_error"
const val STR_ERROR = "error"
const val STR_UPDATE_AVAILABLE = "update_available"
const val STR_YES = "yes"
const val STR_NO = "no"
const val STR_PREPARE_UPDATE = "prepare_update"
const val STR_TRANSPARENT_POINTER = "@pointerTransparent"
const val STR_BLUE_POINTER = "@pointerBlue"
const val STR_WHITE_POINTER = "@pointerWhite"
const val STR_GREEN_POINTER = "@pointerGreen"
const val STR_RESIZE_ELEMENT = "resize_element"
const val STR_RESIZE_ELEMENT_PLR = "resize_element_plr"
const val STR_MOVE_ELEMENT = "move_element"
const val STR_MOVE_ELEMENTS = "move_multiple_elements"
const val STR_OBJECT = "object"
const val STR_OPTIONS = "options"
const val STR_GENERAL = "general"
const val STR_SOFTWARE_LANGUAGE_LABEL = "software_language_lbl"
const val STR_LOOK_AND_FEEL = "look_and_feel"
const val STR_CURSOR_COLOR_LABEL = "cursor_color_lbl"
const val STR_SAVE = "save"
const val STR_RESTORE_DEFAULTS_OPTIONS = "restore_options"
const val STR_CUSTOM_COLOR = "custom_color"
const val STR_PURPLE = "purple"
const val STR_WHITE_WITH_BLACK_BORDER = "white_black"
const val STR_BLACK_WITH_WHITE_BORDER = "black_white"
const val STR_SELECT_COLOR = "select_color"
const val STR_PLAYERFRAME_OPENED = "PlayrFrame_opened"
const val STR_DOUBLE_CLICK_OPEN_ACT = "double_click_act"
const val STR_DEFAULT_ELEMENT_VISIBILITY = "default_element_visibility"
const val STR_YELLOW = "yellow"
const val STR_RED = "red"
const val STR_CANCEL_UPDATE = "cancel_update"
const val STR_LABEL_STATE = "label_state"
const val STR_LABEL = "label"
const val STR_SIZE_COMBO_TOOLTIP = "size_tooltip"
const val STR_PRIORITY_COMBO_TOOLTIP = "priority_tooltip"
const val STR_LABEL_TOOLTIP = "label_tooltip"
const val STR_DB_VERSION_MISMATCH = "db_mismatch"
const val STR_EXIT = "exit"
const val STR_UPDATE = "update"
const val STR_RESET = "reset"
const val STR_BACK = "back"
const val STR_LOADING = "loading"
const val STR_INFO = "info"
const val STR_BLACK = "black"
const val STR_LABEL_COLOR = "label_color"
const val STR_LABEL_DISABLED = "label_disabled"
const val STR_LABEL_HIDDEN = "label_hidden"
const val STR_LABEL_VISIBLE = "label_visible"
const val STR_LABEL_VISIBLE_FOR_MASTER = "label_visible_for_master"

// -- Sentences

const val ST_WARNING_CONFIG_RESET = "warn_conf_reset"
const val ST_CONFIGURATION_IMPORTED = "conf_imported"
const val ST_WARNING_PREVIOUS_VERSION_FILE = "warn_prev_file"
const val ST_WARNING_MISSING_CONF_FILES = "missing_conf_files"
const val ST_UNKNOWN_ERROR = "err_unknown"
const val ST_FILE_MAY_BE_CORRUPTED = "file_corrupted"
const val ST_ACT_ALREADY_EXISTS = "act_already_exists"
const val ST_ACT_WITHOUT_SCENE = "act_no_scene"
const val ST_SCENE_ALREADY_EXISTS_OR_INVALID = "scene_already_exists_invalid"
const val ST_SCENE_ALREADY_EXISTS = "scene_already_exists"
const val ST_FILE_ALREADY_EXISTS = "file_already_exists"
const val ST_ELEMENT_ALREADY_EXISTS_OR_INVALID = "element_already_exists_invalid"
const val ST_ELEMENT_ALREADY_EXISTS = "element_already_exists"
const val ST_CONFIRM_CLEAR_BOARD = "confirm_clear_board"
const val ST_ERROR_LOADING_DATABASE = "error_load_database"
const val ST_NEW_VERSION_AVAILABLE = "new_version_available"
const val ST_UPDATE_OLEBO_RESTART = "update_will_restart_olebo"
const val ST_NEVER_ASK_UPDATE = "no_never_ask_update"
const val ST_ERROR_ACT_NOT_EXISTS = "error_act"
const val ST_UNKNOWN_DATABASE_VERSION = "unknown_database_version"
const val ST_CHANGE_VISIBILITY = "change_visiblity"
const val ST_CHANGE_VISIBILITY_PLR = "change_visiblity_plr"
const val ST_LANGUAGE_CHANGE_ON_RESTART = "change_language_restart"
const val ST_OLEBO_IS_UPDATING = "olebo_is_updating"
const val ST_NOT_TURN_OFF = "dont_turn_off_computer"
const val ST_UPDATE_SUCCESS = "update_success"
const val ST_UPDATE_FAILED = "update_failed"
const val ST_UPDATE_TRY_AGAIN = "update_try_again"
const val ST_DB_VERSION_MISMATCH_MESSAGE = "db_mismatch_message"
const val ST_CANCEL_WILL_ERASE_CHANGES = "action_erase_changes"
const val ST_NAME_OF_BLUEPRINT_REQUIRED = "name_blueprint_required"
const val ST_IMG_BLUEPRINT_REQUIRED = "img_blueprint_required"
const val ST_IMG_BLUEPRINT_NOT_EXIST_OR_INVALID = "img_blueprint_invalid_or_not_exist"
const val ST_OCCURENCE_BLUEPRINT_TO_DELETE = "num_blueprint_to_delete"
const val ST_DEFAULT_SETTINGS_RESTORED = "restored_default_settings"


// -- Interpolable Strings
const val ST_INT1_OCCURENCE_BLUEPRINT_TO_DELETE = "1_num_blueprint_to_delete"