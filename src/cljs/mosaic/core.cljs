(ns mosaic.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [mosaic.events]
              [mosaic.subs]
              [mosaic.views :as views]))


(defn dev-setup []
  (enable-console-print!)
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
