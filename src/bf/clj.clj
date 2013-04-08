(ns bf.clj
  (:refer-clojure :exclude [eval]))

(def default-data-vector-length 30000)

(def left-bracket  \u005b)
(def right-bracket \u005d)

(defn initial-state-map
  [data-vector-length]
  {:data (vec (repeat data-vector-length 0))
   :pc   0
   :dp   0})

(defn parse [^String txt]
  (let [jump-table (loop [open () i 0 t {}]
                     (if (< i (count txt))
                       (condp = (.charAt txt i)
                         left-bracket  (recur (conj open i) (inc i) t)
                         right-bracket (recur (pop open) (inc i)
                                              (-> t
                                                  (assoc (peek open) i)
                                                  (assoc i (peek open))))
                         (recur open (inc i) t))
                       t))]
    {:txt txt :jump-table jump-table}))

(defn current-instruction [prog state]
  (let [txt ^String (:txt prog)]
    (.charAt txt (:pc state))))

(defmacro incrementing-pc [& body]
  `(update-in (do ~@body) [:pc] inc))

(defn jump-target [prog state]
  (get (:jump-table prog) (:pc state)))

(defn print-and-flush [& args]
  (apply print args)
  (flush))

(defmulti step current-instruction)

(defmethod step :default [prog state] ; skip meaningless characters
  (incrementing-pc state))

(defmethod step \< [prog state] ; dec data pointer
  (incrementing-pc
   (update-in state [:dp] dec)))

(defmethod step \> [prog state] ; inc data pointer
  (incrementing-pc
   (update-in state [:dp] inc)))

(defmethod step \- [prog state] ; dec data
  (incrementing-pc
   (update-in state [:data (:dp state)] unchecked-dec)))

(defmethod step \+ [prog state] ; inc data
  (incrementing-pc
   (update-in state [:data (:dp state)] unchecked-inc)))

(defmethod step \. [prog state] ; print
  (incrementing-pc
   (doto state
     (-> :data (nth (:dp state)) char print-and-flush))))

(defmethod step \, [prog state] ; read
  (incrementing-pc
   (let [n (Long/parseLong (read-line))]
     (assoc-in state [:data (:dp state)] n))))

(defmethod step left-bracket [prog state] ; start loop
  (let [end (jump-target prog state)
        d   (nth (:data state) (:dp state))]
    (if (zero? d)
      (assoc-in state [:pc] (inc end))
      (update-in state [:pc] inc))))

(defmethod step right-bracket [prog state] ; end loop
  (let [start (jump-target prog state)
        d     (nth (:data state) (:dp state))]
    (if (zero? d)
      (update-in state [:pc] inc)
      (assoc-in state [:pc] (inc start)))))

(defn eval
  ([prog]
     (eval prog (initial-state-map default-data-vector-length)))
  ([prog state]
     (let [lim (count (:txt prog))]
       (loop [state state]
         (if (< (:pc state) lim)
           (recur (step prog state))
           state)))))

(defn exec
  ([txt]
     (exec txt default-data-vector-length))
  ([txt data-vector-length]
     (eval (parse txt) (initial-state-map data-vector-length))))
