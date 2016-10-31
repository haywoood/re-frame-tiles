(ns mosaic.views
    (:require [re-frame.core :as re-frame]))

(defn tile [{:keys [id color background-color] :as tile} handler]
  ^{:key id}
  [:div {:style {:backgroundColor background-color
                 :width 19 :height 30
                 :position "relative"}
         :onMouseOver #(handler [:on-mouse-over tile])
         :onMouseDown #(handler [:on-mouse-down tile])
         :onMouseUp #(handler [:on-mouse-up tile])}
    [:div {:style {:position "absolute"
                   :bottom 7
                   :left 7
                   :width 4
                   :height 4
                   :borderRadius 2
                   :backgroundColor color}}]])

(defn board []
  (let [tiles (re-frame/subscribe [:tiles])]
    (fn []
      [:div {:style {:width (* 23 19) :marginTop 20
                     :display "flex" :flexWrap "wrap"}}
        (map (fn [_tile]
               (tile _tile (fn [[action tile]]
                             (condp = action
                               :on-mouse-down (re-frame/dispatch-sync [:select-tile tile :key-down])
                               :on-mouse-over (re-frame/dispatch-sync [:select-tile tile :hover])
                               :on-mouse-up   (re-frame/dispatch-sync [:toggle-dragging])
                               nil))))
             @tiles)])))

(defn legend []
  (let [tiles (re-frame/subscribe [:legend])]
    (fn []
      [:div {:style {:display "flex"}}
        (map (fn [_tile]
               (tile _tile (fn [[action tile]]
                             (condp = action
                               :on-mouse-down (re-frame/dispatch-sync [:select-legend-tile tile])
                               nil))))
             @tiles)])))



(defn main-panel []
  [:div {:style {:display "flex" :flexDirection "column" :flex 1
                 :alignItems "center"}}
    [board]
    [legend]])
