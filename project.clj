(defproject triassic "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [net.mikera/core.matrix "0.31.1"]
                 [cljs-webgl "0.1.5-SNAPSHOT"]
                 [com.cemerick/clojurescript.test "0.3.3"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [com.cemerick/clojurescript.test "0.3.3"]]

  :source-paths ["src/cljs"]

  :cljsbuild {
              :builds [{:id "triassic"
                        :source-paths ["src"]
                        :compiler {
                                   :foreign-libs [
                                                  {:file "resources/js/gl-matrix-min.js" :provides ["mat4","mat3","vec3", "quat"]}
                                                  {:file "resources/js/webgl-utils.js" :provides ["WebGLUtils"]}]
                                   :output-to "out/triassic.js"
                                   :output-dir "target"
                                   :optimizations :none
                                   :source-map true}}]})
