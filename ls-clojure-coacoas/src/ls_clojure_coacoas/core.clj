(ns ls-clojure-coacoas.core)
(use 'clj-time.coerce 'clj-time.format )
(require '[clojure.java.io :as io]
         '[clojure.pprint :as pp])
(import 'java.util.Date)

(defn format-date [pattern]
  (fn [date-time] (unparse (formatter pattern) date-time)))

(defn attrs [file]
  {:size (.length file)
   :lastModified ((format-date "MM/dd/yyyy hh:mm a") (from-long (.lastModified file)))
   :name (.getName file)
   :type (cond 
           (.isDirectory file) 'directory
           (.isFile file)      'file
           :else               'link)})
  
(defn file-to-string [attrs]
  (format "%-30s%-12s%10s%10s" (:name attrs) (:size attrs) (:lastModified attrs) (:type attrs)))

(defn list-element-to-string [e]
  (if (:files e)
    (clojure.string/join "\n" (cons (.getAbsolutePath (:dir e)) 
          (map file-to-string (sort-by :name (map attrs (:files e))))))))

(defn list-files [dir]
  {:dir dir :files (vec (.listFiles dir))})
  
(defn get-file-attrs 
  "List files in the specified directories"
  [dirs]
  (map (comp list-files io/as-file) dirs))

(defn -main
  "List files in the files/directories specified in the args"
  [& args]
  (print 
    (clojure.string/join
      "\n"
      (map list-element-to-string (get-file-attrs args)))))

;(print (clojure.string/join "\n" (map list-element-to-string(get-file-attrs ["/home/bcarlson/"]))))