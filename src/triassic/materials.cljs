(ns triassic.materials
  (:require [cljs-webgl.shaders :refer [get-shader create-program get-attrib-location]]
            [cljs-webgl.constants.buffer-object :as buffer-object]))

(def vertex-shader
  "attribute vec3 aVertexPosition;
   attribute vec4 aVertexColor;

   uniform mat4 uMVMatrix;
   uniform mat4 uPMatrix;

   varying vec4 vColor;

   void main(void) {
       gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
       vColor = aVertexColor;
   }")

(defn color-map [gl vertices]
  (let [fragment
        "precision mediump float;

        varying vec4 vColor;

        void main(void) {
            gl_FragColor = vColor;
        }"]
    {:shader (create-program gl fragment)
     :colors (create-buffer gl
                            (ta/float32 vertices
                                        [
                                         ; Front face
                                         1.0, 0.0, 0.0, 1.0,
                                         1.0, 0.0, 0.0, 1.0,
                                         1.0, 0.0, 0.0, 1.0,
                                         1.0, 0.0, 0.0, 1.0,

                                         ; Back face
                                         1.0, 1.0, 0.0, 1.0,
                                         1.0, 1.0, 0.0, 1.0,
                                         1.0, 1.0, 0.0, 1.0,
                                         1.0, 1.0, 0.0, 1.0,

                                         ; Top face
                                         0.0, 1.0, 0.0, 1.0,
                                         0.0, 1.0, 0.0, 1.0,
                                         0.0, 1.0, 0.0, 1.0,
                                         0.0, 1.0, 0.0, 1.0,

                                         ; Bottom face
                                         1.0, 0.5, 0.5, 1.0,
                                         1.0, 0.5, 0.5, 1.0,
                                         1.0, 0.5, 0.5, 1.0,
                                         1.0, 0.5, 0.5, 1.0,

                                         ; Right face
                                         1.0, 0.0, 1.0, 1.0,
                                         1.0, 0.0, 1.0, 1.0,
                                         1.0, 0.0, 1.0, 1.0,
                                         1.0, 0.0, 1.0, 1.0,

                                         ; Left face
                                         0.0, 0.0, 1.0, 1.0,
                                         0.0, 0.0, 1.0, 1.0,
                                         0.0, 0.0, 1.0, 1.0,
                                         0.0, 0.0, 1.0, 1.0 ])
                            buffer-object/array-buffer
                            buffer-object/static-draw
                            4)}))
