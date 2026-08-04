package modules.terrain.roam.tri

import kotlin.collections.ArrayList

class TriNodeQueue {

    private var splitQ = HashSet<TriNode>()
    private var mergeQ = HashSet<TriNode>()

    fun addSplitTri(tri: TriNode) {
        if (!splitQ.contains(tri)) {
            splitQ.add(tri)
        }
    }

    fun removeSplitTri(tri: TriNode) {
        splitQ.remove(tri)
    }

    fun addMergeTri(tri: TriNode) {
        if (!mergeQ.contains(tri)) {
            mergeQ.add(tri)
        }
    }

    fun removeMergeTri(tri: TriNode) {
        mergeQ.remove(tri)
    }

    fun getAllSplitTriangles(): ArrayList<TriNode> {
        return ArrayList(splitQ)
    }

    fun getAllMergeTriangles(): ArrayList<TriNode> {
        return ArrayList(mergeQ)
    }
}