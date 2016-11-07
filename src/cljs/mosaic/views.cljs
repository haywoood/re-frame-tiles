(ns mosaic.views
    (:require [re-frame.core :as re-frame]))

(def ^:const BOARD_WIDTH (* 23 19))
(def ^:const BLUE "#0469bd")

(defn tile
  ([_tile] [tile _tile (fn [])])
  ([{:keys [id color background-color] :as tile} handler]
   [:div {:style {:width 19
                  :height 30
                  :position "relative"
                  :backgroundColor background-color}
          :draggable "false"
          :onMouseOver #(handler % [:on-mouse-over tile])
          :onMouseDown #(handler % [:on-mouse-down tile])
          :onMouseUp   #(handler % [:on-mouse-up tile])}
     [:div {:style {:position "absolute"
                    :bottom 7
                    :left 7
                    :width 4
                    :height 4
                    :borderRadius 2
                    :backgroundColor color}}]]))

(defn handle-board-tile-action
  [e [action tile]]
  (condp = action
    :on-mouse-down (re-frame/dispatch-sync [:select-tile tile :key-down])
    :on-mouse-over (re-frame/dispatch-sync [:select-tile tile :hover])
    :on-mouse-up   (re-frame/dispatch-sync [:toggle-dragging])
    nil))

(defn board []
  (let [tiles (re-frame/subscribe [:tiles])]
    (fn []
      [:div {:style {:width BOARD_WIDTH
                     :display "flex"
                     :flexWrap "wrap"}}
        (map (fn [_tile]
               ^{:key (:id _tile)}
               [tile _tile handle-board-tile-action])
             @tiles)])))

(defn handle-legend-tile-action
  [e [action tile]]
  (condp = action
    :on-mouse-down (re-frame/dispatch-sync [:select-legend-tile tile])
    nil))

(defn legend []
  (let [tiles (re-frame/subscribe [:legend])]
    (fn []
      [:div {:style {:display "flex"}}
        (map (fn [_tile]
               ^{:key (:id _tile)}
               [tile _tile handle-legend-tile-action])
             @tiles)])))

(defn toolbar []
  (let [selected-tile (re-frame/subscribe [:selected-tile])]
    [:div {:style {:display "flex" :width BOARD_WIDTH}}
        [:div {:style {:display "flex" :flex 1}}
          [tile @selected-tile]]
        [:div {:class "Toolbar-link"
               :onClick #(re-frame/dispatch [:save-board])}
           "save"]
        [:div {:class "Toolbar-link"
               :onClick #(re-frame/dispatch-sync [:clear-board])}
          "clear"]]))

(defn preview-text
  [id]
  (let [board-preview-id (re-frame/subscribe [:board-preview-id])]
    (fn []
      (if (= @board-preview-id id)
        [:div {:style {:color "orange" :marginLeft 20}} "previewing"]
        [:div ""]))))

(defn saved-board
  [{:keys [id board name]}]
  ^{:key id}
  [:div {:style {:display "flex" :width BOARD_WIDTH :marginBottom 10}}
    [:div {:style {:flex 1 :display "flex"}
           :onMouseOver #(re-frame/dispatch [:preview-saved-board id])
           :onMouseOut #(re-frame/dispatch [:remove-preview-saved-board])}
      name
      [preview-text id]]
    [:button {:onClick #(re-frame/dispatch [:load-saved-board id])
              :className "hand-on-hover"}
      "LOAD"]
    [:button {:onClick #(re-frame/dispatch [:delete-saved-board id])
              :className "hand-on-hover"
              :style {:color "red"}}
      "DELETE"]])

(defn saved-boards []
  (let [boards (re-frame/subscribe [:saved-boards])]
    (fn []
      [:div {:style {:marginTop 40 :color BLUE}}
        "Saved boards"
        [:hr]
        (map saved-board @boards)])))

(defn spacer [amount] [:div {:style {:height amount}}])

(defn main-panel []
  (let [clear-interval (js/setInterval #(re-frame/dispatch [:save-state]) 1000)]
    [:div {:style {:display "flex" :flexDirection "column" :flex 1
                   :alignItems "center"}}
     [spacer 20]
     [toolbar]
     [spacer 5]
     [board]
     [spacer 20]
     [legend]
     [saved-boards]]))
