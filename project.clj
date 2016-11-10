(defproject mosaic "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [reagent "0.6.0"]
                 [re-frame "0.8.0"]
                 [hodgepodge "0.1.3"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:nrepl-port 5555
             :css-dirs ["resources/public/css"]
             :ring-handler mosaic.core/handler}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.8.2"]
                   [figwheel "0.5.4-4"]
                   [proto-repl "0.3.1"]
                   [figwheel-sidecar "0.5.4-4"]
                   [com.cemerick/piggieback "0.2.1"]
                   [org.clojure/tools.namespace "0.2.11"]
                   [org.clojure/tools.nrepl "0.2.12"]]

    :source-paths ["cljs_src" "dev"]

    :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

    :plugins      [[lein-figwheel "0.5.7"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "mosaic.core/mount-root"}
     :compiler     {:main                 mosaic.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            mosaic.core
                    :output-to       "resources/public/js/fuck/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})
