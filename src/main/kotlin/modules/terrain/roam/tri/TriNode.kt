package modules.terrain.roam.tri

import core.scene.Transform
import modules.terrain.heightmap.Heightmap

class TriNode {
    var baseNeighbour: TriNode? = null
    var leftNeighbour: TriNode? = null
    var rightNeighbour: TriNode? = null
    var leftChild: TriNode? = null
    var rightChild: TriNode? = null
    var parent: TriNode? = null

    private var initialized = false
    var index = 0
    var depth = 0

    private lateinit var heightmap: Heightmap
    private lateinit var pool: TriNodePool
    lateinit var geometry: TriNodeGeometry

    private lateinit var queue: TriNodeQueue
    var splitQIndex: Int = -1
    var mergeQIndex: Int = -1

    fun initialize(
        heightmap: Heightmap,
        pool: TriNodePool,
        vertexProvider: TriLocalVerticesProvider,
        transform: Transform,
        queue: TriNodeQueue
    ) {
        if (initialized) {
            return
        }

        this.heightmap = heightmap
        this.pool = pool
        this.geometry = TriNodeGeometry(this, heightmap, transform, vertexProvider)
        this.queue = queue

        initialized = true
    }

    fun recursiveSplitToTargetLod(targetLod: Int) {
        if (depth < targetLod) {
            this.split()
        }
        if (leftChild != null) {
            leftChild!!.recursiveSplitToTargetLod(targetLod)
        }
        if (rightChild != null) {
            rightChild!!.recursiveSplitToTargetLod(targetLod)
        }
    }

    fun recursiveMergeToTargetLod(targetLod: Int) {
        if (depth >= targetLod) {
            merge()
        }

        if (leftChild != null) {
            leftChild!!.recursiveMergeToTargetLod(targetLod)
        }

        if (rightChild != null) {
            rightChild!!.recursiveMergeToTargetLod(targetLod)
        }
    }

    fun isClear(): Boolean {
        return leftChild == null &&
                rightChild == null
                && parent == null
                && leftNeighbour == null
                && rightNeighbour == null
                && baseNeighbour == null
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    fun isLeaf(): Boolean {
        return leftChild == null && rightChild == null
    }

    fun isRoot(): Boolean {
        return parent == null
    }

    fun split() {
        // We are already split, no need to do it again.
        if (leftChild != null) {
            return
        }

        // If this triangle is not in a proper diamond, force split our base neighbor
        if (baseNeighbour != null && baseNeighbour!!.baseNeighbour !== this) {
            baseNeighbour!!.split()
        }

        // Create children and link into mesh
        makeChildren()

        // If creation failed, just exit.
        if (leftChild == null || rightChild == null) {
            leftChild = null
            rightChild = null
            return
        }

        val left = leftChild!!
        val right = rightChild!!

        // Fill in the information we can get from the parent (neighbor pointers)
        left.baseNeighbour = leftNeighbour
        left.leftNeighbour = right

        right.baseNeighbour = rightNeighbour
        right.rightNeighbour = left

        // Link our Left Neighbor to the new children
        if (leftNeighbour != null) {
            val nbr = leftNeighbour!!
            when {
                nbr.baseNeighbour === this -> nbr.baseNeighbour = left
                nbr.leftNeighbour === this -> nbr.leftNeighbour = left
                nbr.rightNeighbour === this -> nbr.rightNeighbour = left
                else -> {
                    println("Warning: Left Neighbor does not reference this node correctly.")
                }
            }
        }

        // Link our Right Neighbor to the new children
        if (rightNeighbour != null) {
            val nbr = rightNeighbour!!
            when {
                nbr.baseNeighbour === this -> nbr.baseNeighbour = right
                nbr.rightNeighbour === this -> nbr.rightNeighbour = right
                nbr.leftNeighbour === this -> nbr.leftNeighbour = right
                else -> {
                    println("Warning: Right Neighbor does not reference this node correctly.")
                }
            }
        }

        // Link our Base Neighbor to the new children
        if (baseNeighbour != null) {
            if (baseNeighbour!!.leftChild != null) {
                // Base Neighbor is already split: link children across the diamond
                val baseLeft = baseNeighbour!!.leftChild!!
                val baseRight = baseNeighbour!!.rightChild!!

                baseLeft.rightNeighbour = right
                baseRight.leftNeighbour = left

                left.rightNeighbour = baseRight
                right.leftNeighbour = baseLeft
            } else {
                // Base Neighbor (in a diamond with us) was not split yet, so do that now.
                baseNeighbour!!.split()
            }
        } else {
            // An edge triangle, trivial case.
            left.rightNeighbour = null
            right.leftNeighbour = null
        }

        if (parent != null) {
            queue.removeMergeTri(parent!!)
        }

        queue.addMergeTri(this)
        queue.removeSplitTri(this)
        queue.addSplitTri(leftChild!!)
        queue.addSplitTri(rightChild!!)

        // BUILD GEOMETRY CACHE
        leftChild!!.geometry.collectMeshData()
        rightChild!!.geometry.collectMeshData()
        geometry.releaseMeshData()
    }

    private fun makeChildren() {
        if (leftChild != null || rightChild != null) {
            return
        }

        leftChild = pool.allocate((index shl 1) + 1)
        rightChild = pool.allocate((index shl 1) + 2)

        // parent
        leftChild!!.parent = this
        rightChild!!.parent = this

        // additional data
        leftChild!!.depth = depth + 1
        rightChild!!.depth = depth + 1

        // SET GEOMETRY
        leftChild!!.initialize(
            heightmap,
            pool,
            { fromParentVerticesProvider(geometry.localVertices, true) },
            geometry.worldTransform,
            queue
        )
        rightChild!!.initialize(
            heightmap,
            pool,
            { fromParentVerticesProvider(geometry.localVertices, false) },
            geometry.worldTransform,
            queue
        )
    }

    //
    // merge down goes in the tree and when leafs are found they are
    // deleted so the parent node remains
    //
    fun merge() {
        // leaf ?
        if (leftChild == null && rightChild == null) {
            return
        }
        if (goodForMerge()) {
            if (baseNeighbour == null) {
                // no diamond
                // at the border - trivial merge
                actualMerge()
            } else {

                // diamond
                // check base for good children
                if (baseNeighbour!!.goodForMerge()) {
                    baseNeighbour!!.actualMerge()
                    actualMerge()
                    return
                }

                // base diamond neighbour is not ready for merge.
                return
            }
            return
        }

        // merge down until we find some leafs.
        leftChild!!.merge()
        rightChild!!.merge()
    }

    //
    // GoodForMerge determines if this node's children are leaves and whether
    // they are ready to be merged (i.e. the variance is high enough)
    //
    private fun goodForMerge(): Boolean {
        // already merged ?
        if (leftChild == null && rightChild == null) {
            return false
        }

        // there are no grandchildren
        return leftChild!!.leftChild == null && rightChild!!.rightChild == null
    }

    //
    // merges the children of the specified triangle node.
    //
    private fun actualMerge() {
        if (leftChild!!.baseNeighbour != null) {
            if (leftChild!!.baseNeighbour!!.leftNeighbour === leftChild) {
                leftChild!!.baseNeighbour!!.leftNeighbour = this
            }
            if (leftChild!!.baseNeighbour!!.rightNeighbour === leftChild) {
                leftChild!!.baseNeighbour!!.rightNeighbour = this
            }
            if (leftChild!!.baseNeighbour!!.baseNeighbour === leftChild) {
                leftChild!!.baseNeighbour!!.baseNeighbour = this
                if (leftNeighbour === leftChild!!.baseNeighbour!!.parent) {
                    leftNeighbour = leftChild!!.baseNeighbour
                }
            }

            //parent of the base neighbor of the left child should be
            //checked
            var par = leftChild!!.baseNeighbour
            if (par!!.parent.also { par = it } != null) {
                if (par!!.leftNeighbour === leftChild) {
                    par!!.leftNeighbour = this
                }
                if (par!!.rightNeighbour === leftChild) {
                    par!!.rightNeighbour = this
                }
                if (par!!.baseNeighbour === leftChild) par!!.baseNeighbour = this
            }
        }

        //same for the right child
        if (rightChild!!.baseNeighbour != null) {
            if (rightChild!!.baseNeighbour!!.leftNeighbour === rightChild) {
                rightChild!!.baseNeighbour!!.leftNeighbour = this
            }
            if (rightChild!!.baseNeighbour!!.rightNeighbour === rightChild) {
                rightChild!!.baseNeighbour!!.rightNeighbour = this
            }
            if (rightChild!!.baseNeighbour!!.baseNeighbour === rightChild) {
                rightChild!!.baseNeighbour!!.baseNeighbour = this
                if (rightNeighbour === rightChild!!.baseNeighbour!!.parent) {
                    rightNeighbour = rightChild!!.baseNeighbour
                }
            }
            var par = rightChild!!.baseNeighbour
            if (par!!.parent.also { par = it } != null) {
                if (par!!.leftNeighbour === rightChild) {
                    par!!.leftNeighbour = this
                }
                if (par!!.rightNeighbour === rightChild) {
                    par!!.rightNeighbour = this
                }
                if (par!!.baseNeighbour === rightChild) {
                    par!!.baseNeighbour = this
                }
            }
        }

        queue.removeSplitTri(leftChild!!)
        queue.removeSplitTri(rightChild!!)
        queue.removeMergeTri(this)
        queue.addSplitTri(this)

        if (parent != null) {
            queue.addMergeTri(parent!!)
        }

        leftChild!!.geometry.releaseMeshData()
        rightChild!!.geometry.releaseMeshData()
        geometry.collectMeshData()

        pool.release(leftChild!!.index)
        pool.release(rightChild!!.index)

        leftChild = null
        rightChild = null
    }

    fun clear() {
        leftChild = null
        rightChild = null
        parent = null
        leftNeighbour = null
        rightNeighbour = null
        baseNeighbour = null
    }
}