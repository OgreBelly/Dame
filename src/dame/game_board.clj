(ns dame.game-board
  (:require [clojure2d.core :as c2d]
            [clojure.string :as s]))

(defrecord Board [canvas window])

(def current-board (atom nil))

(defmulti game (fn [type ^Board board data] type))

(defmethod game :default [])

(def ^:private board-size 1000)
(def ^:private tile-size 125)

(def ^:private player-color {:player1 [:white :black]
                             :player2 [:black :white]})

(defn draw-square
  ([^Board board x y color] (draw-square board x y color true))
  ([^Board board x y color fill]
   (c2d/with-canvas-> (:canvas board)
     (c2d/set-color color)
     (c2d/set-stroke 8)
     (c2d/rect (* x tile-size) (* y tile-size) tile-size tile-size (not fill))
     (c2d/set-color :red)
     (c2d/text (str "(" x "," y ")") (* x tile-size) (- (* (inc y) tile-size) 10)))))

(defn draw-stone
  [^Board board x y player]
  (let [x0 (+ (* x tile-size) (* 0.5 tile-size))
        y0 (+ (* y tile-size) (* 0.5 tile-size))
        s0 (* tile-size 0.82)
        s1 (* tile-size 0.8)
        s2 (* tile-size 0.75)
        s3 (* tile-size 0.55)
        s4 (* tile-size 0.45)]
    (c2d/with-canvas-> (:canvas board)
      (c2d/set-color :white)
      (c2d/ellipse x0 y0 s0 s0)
      (c2d/set-color :black)
      (c2d/ellipse x0 y0 s1 s1)
      (c2d/set-color (->> player-color (player) (first)))
      (c2d/ellipse x0 y0 s2 s2)
      (c2d/set-color (->> player-color (player) (second)))
      (c2d/ellipse x0 y0 s3 s3)
      (c2d/set-color (->> player-color (player) (first)))
      (c2d/ellipse x0 y0 s4 s4))))

(defn draw-dame-sign
  "Draws an upsite down cross inside a D at the current tile"
  [^Board board x y player]
  (let [x0 (+ (* x tile-size) (* 0.5 tile-size))
        y0 (+ (* y tile-size) (* 0.5 tile-size))]
    (c2d/with-canvas-> (:canvas board)
      (c2d/set-color (->> player-color (player) (second)))
      (c2d/set-font-attributes 48)
      (c2d/text "D" (inc x0) (+ y0 18) :center)
      (c2d/set-stroke 3)
      (c2d/line x0 (- y0 15) x0 (+ y0 15))
      (c2d/line (- x0 10) (+ y0 5) (+ x0 10) (+ y0 5))
      )))

(defn draw-game
  "game is a 8 element vector with each element being a 8 element vector"
  [^Board board game]
  (doseq [x (range 8)
          y (range 8)]
    (let [stone ((game y) x)
          color [:white :black]
          color-indicator (mod (+ x y) 2)]
      (if (and (:selected stone) (:selection-color stone))
        (draw-square board x y (:selection-color stone) false)
        (draw-square board x y (nth color color-indicator)))
      (let [stones (seq (:player stone))
            player (nth stones 0)]
        (when player
          (draw-stone board x y player)
          (when (> (count stones) 1)
            (draw-dame-sign board x y player)))))))

(defn show-player-label
  [^Board board player]
  (let [color (name (first (player @player-color)))
        color (concat (list (s/upper-case (first color))) (rest color))
        color (apply str color)]
    (println color)
  (c2d/with-canvas-> (:canvas board)
    (c2d/set-color :red)
    (c2d/set-font-attributes 12)
    (c2d/text "color" 10 50))))

(defn select-stone
  [^Board board x y]
  (draw-square board x y :green false))

(defn create-board
  []
  (let [canvas (c2d/canvas board-size board-size)
        new-board (Board. canvas (c2d/show-window canvas "Dame"))]
    ;; would be nice if clojure2d would have a function to get active windows
    (reset! current-board new-board)
    new-board))

(defn get-tile
  "Returns the tile that contains the given x and y coordinates"
  [x-coord y-coord]
  (let [x (Math/floor (/ x-coord tile-size))
        y (Math/floor (/ y-coord tile-size))]
    [(int x) (int y)]))

(defmethod c2d/mouse-event ["Dame" :mouse-pressed]
  [event state]
  (let [window (:window @current-board)
        x (c2d/mouse-x window)
        y (c2d/mouse-y window)]
    (game :tile-clicked @current-board (get-tile x y))))