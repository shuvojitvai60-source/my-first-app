package com.example.data

import com.example.model.Game
import com.example.model.GameCategory
import com.example.model.GameType

object GameRegistry {
    private val _games = mutableListOf<Game>()

    val games: List<Game>
        get() = _games

    val featuredGames: List<Game>
        get() = _games.filter { it.id in listOf(1, 3, 11, 14, 16, 20, 26, 45, 53, 56) }

    init {
        registerAllGames()
    }

    fun register(game: Game) {
        _games.removeAll { it.id == game.id }
        _games.add(game)
    }

    fun getGame(id: Int): Game? {
        return _games.find { it.id == id }
    }

    fun getGamesByCategory(category: GameCategory): List<Game> {
        if (category == GameCategory.ALL) return _games
        return _games.filter { it.category == category }
    }

    fun searchGames(query: String, category: GameCategory = GameCategory.ALL): List<Game> {
        val baseList = getGamesByCategory(category)
        if (query.isBlank()) return baseList
        val cleanQuery = query.trim().lowercase()
        return baseList.filter {
            it.name.lowercase().contains(cleanQuery) ||
            it.category.title.lowercase().contains(cleanQuery) ||
            it.description.lowercase().contains(cleanQuery) ||
            it.number.lowercase().contains(cleanQuery)
        }
    }

    private fun registerAllGames() {
        val gameList = listOf(
            Game(1, "Game 01", "Tic Tac Toe", GameCategory.PUZZLE, "Classic X and O tactical game", "❌", GameType.TIC_TAC_TOE),
            Game(2, "Game 02", "Rock Paper Scissors", GameCategory.CASUAL, "Battle the AI hand signs", "✂️", GameType.ROCK_PAPER_SCISSORS),
            Game(3, "Game 03", "Snake", GameCategory.ARCADE, "Eat glowing dots and grow your tail", "🐍", GameType.SNAKE),
            Game(4, "Game 04", "Memory Match", GameCategory.BRAIN, "Flip and match twin emoji cards", "🧠", GameType.MEMORY_MATCH),
            Game(5, "Game 05", "Number Guess", GameCategory.BRAIN, "Guess secret digits with hints", "🔢", GameType.NUMBER_GUESS),
            Game(6, "Game 06", "Tap Challenge", GameCategory.ACTION, "Fastest fingers tap challenge", "👆", GameType.TAP_CHALLENGE),
            Game(7, "Game 07", "Color Match", GameCategory.BRAIN, "Pick matching color or text meaning", "🎨", GameType.COLOR_MATCH),
            Game(8, "Game 08", "Quick Math", GameCategory.BRAIN, "Solve instant arithmetic puzzles", "➗", GameType.QUICK_MATH),
            Game(9, "Game 09", "Word Guess", GameCategory.PUZZLE, "Guess the hidden 5-letter word", "🔤", GameType.WORD_GUESS),
            Game(10, "Game 10", "Connect Four", GameCategory.STRATEGY, "Drop tokens to connect 4 in a line", "🔴", GameType.CONNECT_FOUR),
            Game(11, "Game 11", "2048", GameCategory.PUZZLE, "Slide numbers to reach the 2048 tile", "🎴", GameType.GAME_2048),
            Game(12, "Game 12", "Minesweeper", GameCategory.STRATEGY, "Sweep the minefield safely", "💣", GameType.MINESWEEPER),
            Game(13, "Game 13", "Brick Breaker", GameCategory.ARCADE, "Bounce paddle ball to shatter bricks", "🧱", GameType.BRICK_BREAKER),
            Game(14, "Game 14", "Flappy Obstacle", GameCategory.ARCADE, "Flap through neon pipe obstacles", "🐥", GameType.FLAPPY_BIRD),
            Game(15, "Game 15", "Endless Runner", GameCategory.ACTION, "Run, jump and leap over spikes", "🏃", GameType.ENDLESS_RUNNER),
            Game(16, "Game 16", "Mini Racing", GameCategory.RACING, "Drive and avoid obstacles at speed", "🏎️", GameType.MINI_RACING),
            Game(17, "Game 17", "Car Dodge", GameCategory.RACING, "Dodge opposing highway traffic", "🚗", GameType.CAR_DODGE),
            Game(18, "Game 18", "Basketball Shot", GameCategory.SPORTS, "Swipe and shoot hoops for points", "🏀", GameType.BASKETBALL_SHOT),
            Game(19, "Game 19", "Football Penalty", GameCategory.SPORTS, "Aim and kick past the goalkeeper", "⚽", GameType.FOOTBALL_PENALTY),
            Game(20, "Game 20", "Cricket Batting", GameCategory.SPORTS, "Time your swing to hit big boundaries", "🏏", GameType.CRICKET_BATTING),
            Game(21, "Game 21", "Cricket Target", GameCategory.SPORTS, "Hit the target stumps accurately", "🎯", GameType.CRICKET_TARGET),
            Game(22, "Game 22", "Table Tennis", GameCategory.SPORTS, "Rally paddle ball against the bot", "🏓", GameType.TABLE_TENNIS),
            Game(23, "Game 23", "Air Hockey", GameCategory.SPORTS, "Glide the puck into opponent's net", "🏒", GameType.AIR_HOCKEY),
            Game(24, "Game 24", "Bowling", GameCategory.SPORTS, "Roll bowling ball to knock down pins", "🎳", GameType.BOWLING),
            Game(25, "Game 25", "Archery", GameCategory.SPORTS, "Aim arrow directly at bullseye", "🏹", GameType.ARCHERY),
            Game(26, "Game 26", "Knife Target", GameCategory.ACTION, "Throw spinning blades onto target", "🗡️", GameType.KNIFE_TARGET),
            Game(27, "Game 27", "Bubble Shooter", GameCategory.ARCADE, "Shoot and pop vibrant bubble clusters", "🫧", GameType.BUBBLE_SHOOTER),
            Game(28, "Game 28", "Block Puzzle", GameCategory.PUZZLE, "Fit colorful blocks to clear lines", "🧩", GameType.BLOCK_PUZZLE),
            Game(29, "Game 29", "Sliding Puzzle", GameCategory.PUZZLE, "Slide tiles into sequential order", "🔢", GameType.SLIDING_PUZZLE),
            Game(30, "Game 30", "Sudoku", GameCategory.BRAIN, "Fill 4x4 mini Sudoku grid numbers", "9️⃣", GameType.SUDOKU),
            Game(31, "Game 31", "Crossword", GameCategory.PUZZLE, "Solve thematic word clues", "📝", GameType.CROSSWORD),
            Game(32, "Game 32", "Word Scramble", GameCategory.BRAIN, "Unscramble letters to find the word", "🔠", GameType.WORD_SCRAMBLE),
            Game(33, "Game 33", "Hangman", GameCategory.PUZZLE, "Guess the secret word letter by letter", "🪢", GameType.HANGMAN),
            Game(34, "Game 34", "Math Challenge", GameCategory.BRAIN, "Fast-paced mental math equations", "➕", GameType.MATH_CHALLENGE),
            Game(35, "Game 35", "Addition Challenge", GameCategory.BRAIN, "Rapid sum addition calculations", "➕", GameType.ADDITION_CHALLENGE),
            Game(36, "Game 36", "Multiplication Challenge", GameCategory.BRAIN, "Rapid multiplication arithmetic", "✖️", GameType.MULTIPLICATION_CHALLENGE),
            Game(37, "Game 37", "Memory Numbers", GameCategory.BRAIN, "Remember and recall flashing numbers", "👁️", GameType.MEMORY_NUMBERS),
            Game(38, "Game 38", "Reaction Test", GameCategory.ACTION, "Tap instantly when the screen turns green", "⚡", GameType.REACTION_TEST),
            Game(39, "Game 39", "Speed Tap", GameCategory.ACTION, "Tap as fast as possible in 10 seconds", "⏱️", GameType.SPEED_TAP),
            Game(40, "Game 40", "Tap the Target", GameCategory.ACTION, "Tap moving targets before they vanish", "🎯", GameType.TAP_THE_TARGET),
            Game(41, "Game 41", "Catch the Ball", GameCategory.ARCADE, "Move basket to catch falling balls", "🧺", GameType.CATCH_THE_BALL),
            Game(42, "Game 42", "Avoid the Bomb", GameCategory.ACTION, "Collect coins while avoiding bombs", "💣", GameType.AVOID_THE_BOMB),
            Game(43, "Game 43", "Jump Game", GameCategory.ARCADE, "Leap higher on bouncy platforms", "🦘", GameType.JUMP_GAME),
            Game(44, "Game 44", "Platform Jumper", GameCategory.ARCADE, "Navigate ascending cloud platforms", "🪜", GameType.PLATFORM_JUMPER),
            Game(45, "Game 45", "Space Shooter", GameCategory.ACTION, "Shoot enemy spaceships across galaxies", "🚀", GameType.SPACE_SHOOTER),
            Game(46, "Game 46", "Alien Shooter", GameCategory.ACTION, "Defend Earth from alien invaders", "👾", GameType.ALIEN_SHOOTER),
            Game(47, "Game 47", "Asteroid Dodge", GameCategory.ACTION, "Steer spaceship through asteroid field", "☄️", GameType.ASTEROID_DODGE),
            Game(48, "Game 48", "Zombie Escape", GameCategory.ACTION, "Outrun chasing zombie hordes", "🧟", GameType.ZOMBIE_ESCAPE),
            Game(49, "Game 49", "Tower Defense", GameCategory.STRATEGY, "Place defensive turrets against creeps", "🏰", GameType.TOWER_DEFENSE),
            Game(50, "Game 50", "Maze Escape", GameCategory.PUZZLE, "Navigate maze corridors to the exit", "🌀", GameType.MAZE_ESCAPE),
            Game(51, "Game 51", "Treasure Hunt", GameCategory.STRATEGY, "Dig coordinates to find gold treasure", "🗺️", GameType.TREASURE_HUNT),
            Game(52, "Game 52", "Coin Collector", GameCategory.CASUAL, "Gather golden coins before time expires", "🪙", GameType.COIN_COLLECTOR),
            Game(53, "Game 53", "Color Switch", GameCategory.ARCADE, "Pass only through matching color gates", "🌈", GameType.COLOR_SWITCH),
            Game(54, "Game 54", "Match 3", GameCategory.CASUAL, "Swap gems to match three in a line", "💎", GameType.MATCH_3),
            Game(55, "Game 55", "Fruit Slice", GameCategory.ACTION, "Slice soaring juicy fruits in midair", "🍉", GameType.FRUIT_SLICE),
            Game(56, "Game 56", "Balloon Pop", GameCategory.CASUAL, "Pop floating festive balloons quickly", "🎈", GameType.BALLOON_POP),
            Game(57, "Game 57", "Bubble Pop", GameCategory.CASUAL, "Tap soapy bubbles before they float away", "🫧", GameType.BUBBLE_POP),
            Game(58, "Game 58", "Stack Blocks", GameCategory.ARCADE, "Stack shifting 3D blocks with precision", "📦", GameType.STACK_BLOCKS),
            Game(59, "Game 59", "Tower Stack", GameCategory.ARCADE, "Build the tallest skyscraper tower", "🗼", GameType.TOWER_STACK),
            Game(60, "Game 60", "Knife Throw", GameCategory.ACTION, "Fling knives into spinning wooden log", "🗡️", GameType.KNIFE_THROW),
            Game(61, "Game 61", "Fishing", GameCategory.CASUAL, "Reel in rare fish at the perfect moment", "🎣", GameType.FISHING),
            Game(62, "Game 62", "Car Parking", GameCategory.RACING, "Steer precisely into tight parking spots", "🅿️", GameType.CAR_PARKING),
            Game(63, "Game 63", "Traffic Dodge", GameCategory.RACING, "Weave through bumper-to-bumper city traffic", "🚦", GameType.TRAFFIC_DODGE),
            Game(64, "Game 64", "Bike Racing", GameCategory.RACING, "Throttle superbike on winding asphalt", "🏍️", GameType.BIKE_RACING),
            Game(65, "Game 65", "Boat Racing", GameCategory.RACING, "Speedboat sprint across ocean waves", "🚤", GameType.BOAT_RACING),
            Game(66, "Game 66", "Plane Dodge", GameCategory.RACING, "Pilot turbo aircraft dodging storms", "✈️", GameType.PLANE_DODGE),
            Game(67, "Game 67", "Space Runner", GameCategory.ACTION, "Futuristic cyber sprint along neon rails", "🌌", GameType.SPACE_RUNNER),
            Game(68, "Game 68", "Basketball Dunk", GameCategory.SPORTS, "Slam dunk through swinging hoops", "🏀", GameType.BASKETBALL_DUNK),
            Game(69, "Game 69", "Football Goal", GameCategory.SPORTS, "Curve freekicks right into top corners", "🥅", GameType.FOOTBALL_GOAL),
            Game(70, "Game 70", "Cricket Six", GameCategory.SPORTS, "Power hit balls out of the stadium", "🏏", GameType.CRICKET_SIX),
            Game(71, "Game 71", "Cricket Bowling", GameCategory.SPORTS, "Bowl spin deliveries to shatter stumps", "🏏", GameType.CRICKET_BOWLING),
            Game(72, "Game 72", "Tennis Hit", GameCategory.SPORTS, "Smash powerful tennis returns on court", "🎾", GameType.TENNIS_HIT),
            Game(73, "Game 73", "Golf Mini", GameCategory.SPORTS, "Putt golf ball past crazy windmills", "⛳", GameType.GOLF_MINI),
            Game(74, "Game 74", "Boxing Tap", GameCategory.SPORTS, "Rapid jab punches to KO your sparring bot", "🥊", GameType.BOXING_TAP),
            Game(75, "Game 75", "Wrestling Tap", GameCategory.SPORTS, "Tap power bar to pin opponent wrestler", "🤼", GameType.WRESTLING_TAP),
            Game(76, "Game 76", "Chess Puzzle", GameCategory.STRATEGY, "Find checkmate in one tactical move", "♟️", GameType.CHESS_PUZZLE),
            Game(77, "Game 77", "Checkers", GameCategory.STRATEGY, "Jump diagonal checkers pieces to capture", "🏁", GameType.CHECKERS),
            Game(78, "Game 78", "Word Search", GameCategory.PUZZLE, "Spot hidden gaming words in the grid", "🔍", GameType.WORD_SEARCH),
            Game(79, "Game 79", "Pattern Match", GameCategory.BRAIN, "Memorize and repeat color flash patterns", "❇️", GameType.PATTERN_MATCH),
            Game(80, "Game 80", "Find the Difference", GameCategory.BRAIN, "Spot the subtle difference between sides", "🧐", GameType.FIND_THE_DIFFERENCE),
            Game(81, "Game 81", "Find the Odd One", GameCategory.BRAIN, "Identify the single odd emoji in the flock", "👀", GameType.FIND_THE_ODD_ONE),
            Game(82, "Game 82", "Memory Cards", GameCategory.BRAIN, "Test visual memory matching deck cards", "🃏", GameType.MEMORY_CARDS),
            Game(83, "Game 83", "Simon Says", GameCategory.BRAIN, "Echo the 4-color audio-visual sequence", "🟢", GameType.SIMON_SAYS),
            Game(84, "Game 84", "Color Memory", GameCategory.BRAIN, "Remember sequence of flashing colors", "🟪", GameType.COLOR_MEMORY),
            Game(85, "Game 85", "Shape Match", GameCategory.BRAIN, "Match geometric silhouettes in time", "🔷", GameType.SHAPE_MATCH),
            Game(86, "Game 86", "Quick Reaction", GameCategory.ACTION, "Lightning quick tap when signal fires", "⚡", GameType.QUICK_REACTION),
            Game(87, "Game 87", "Brain Test", GameCategory.BRAIN, "Tricky riddle questions that test lateral thought", "💡", GameType.BRAIN_TEST),
            Game(88, "Game 88", "Logic Puzzle", GameCategory.BRAIN, "Deduce correct sequence through logic", "🧩", GameType.LOGIC_PUZZLE),
            Game(89, "Game 89", "IQ Challenge", GameCategory.BRAIN, "Complete pattern sequence intelligence test", "🧠", GameType.IQ_CHALLENGE),
            Game(90, "Game 90", "Number Puzzle", GameCategory.BRAIN, "Arrange numbers to reach target equations", "🔢", GameType.NUMBER_PUZZLE),
            Game(91, "Game 91", "Sequence Puzzle", GameCategory.BRAIN, "Find the next missing number in sequence", "➡️", GameType.SEQUENCE_PUZZLE),
            Game(92, "Game 92", "Emoji Guess", GameCategory.CASUAL, "Decode the movie, game or idiom from emojis", "🤔", GameType.EMOJI_GUESS),
            Game(93, "Game 93", "Flag Guess", GameCategory.BRAIN, "Identify world country from its national flag", "🚩", GameType.FLAG_GUESS),
            Game(94, "Game 94", "Animal Guess", GameCategory.CASUAL, "Guess the creature from traits and sound clues", "🦁", GameType.ANIMAL_GUESS),
            Game(95, "Game 95", "Food Guess", GameCategory.CASUAL, "Guess famous international dishes and snacks", "🍕", GameType.FOOD_GUESS),
            Game(96, "Game 96", "Movie Guess", GameCategory.CASUAL, "Name blockbuster films from iconic quotes", "🎬", GameType.MOVIE_GUESS),
            Game(97, "Game 97", "General Knowledge Quiz", GameCategory.BRAIN, "Trivia across world science, art and history", "🌍", GameType.GENERAL_KNOWLEDGE_QUIZ),
            Game(98, "Game 98", "Sports Quiz", GameCategory.SPORTS, "Test your sports champions trivia knowledge", "🏆", GameType.SPORTS_QUIZ),
            Game(99, "Game 99", "Geography Quiz", GameCategory.BRAIN, "World capitals, rivers and mountain trivia", "🗺️", GameType.GEOGRAPHY_QUIZ),
            Game(100, "Game 100", "Rapid Quiz", GameCategory.BRAIN, "Fast-fire 60-second multi-topic quiz", "⚡", GameType.RAPID_QUIZ),
            Game(101, "Game 101", "Endless Tap", GameCategory.NEW, "Endless tapping score booster arena", "👆", GameType.ENDLESS_TAP),
            Game(102, "Game 102", "Lucky Number", GameCategory.NEW, "Spin the gaming roulette for high score", "🎰", GameType.LUCKY_NUMBER)
        )

        _games.clear()
        _games.addAll(gameList)
    }
}
