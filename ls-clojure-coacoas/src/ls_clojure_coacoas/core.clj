(ns ls-clojure-coacoas.core)
(require '[clojure.java.io :as io])

(defn directory? [dir] (.isDirectory dir))

(defn attrs [file]
  {:size (.length file)
   :lastModified (.lastModified file)
   :name (.getName file)
   :type (cond 
           (.isDirectory file) 'directory
           (.isFile file)      'file
           :else               'link)})

(defn show [full? file]
  (if full? 
    (clojure.string/join \tab (map #(% file) [:name :size :lastModified :type]))
    (:name file)))

(defn files [dir]
  {:dir dir 
   :files (.listFiles dir)})

(defn list-files 
  "List files in the specified directories"
  [dirs]
  (map (comp files io/as-file) dirs))

(defn -main
  "List files in the files/directories specified in the args"
  [& args]
  (list-files args))
