package modules.terrain.roam.tri

class TriNodeQueue {

    private var splitQ: Array<TriNode?> = arrayOfNulls(TriNodeGeometry.maxLeafTriangles)
    private var mergeQ: Array<TriNode?> = arrayOfNulls(TriNodeGeometry.maxLeafTriangles / 2)

    private var splitQSize: Int = 0
    private var mergeQSize: Int = 0

    fun addSplitTri(tri: TriNode) {
        if (tri.splitQIndex == -1) {
            tri.splitQIndex = splitQSize
            splitQ[splitQSize++] = tri
        }
    }

    fun removeSplitTri(tri: TriNode) {
        if (splitQSize == 0) {
            return
        }

        if (tri.splitQIndex > splitQSize - 1) {
            return
        }

        if (tri.splitQIndex != -1) {
            // SWAP WITH LAST
            val last = splitQ[splitQSize - 1]
            splitQ[tri.splitQIndex] = last
            last!!.splitQIndex = tri.splitQIndex
            tri.splitQIndex = -1
            splitQSize--
        }
    }

    fun addMergeTri(tri: TriNode) {
        if (tri.mergeQIndex == -1) {
            tri.mergeQIndex = mergeQSize
            mergeQ[mergeQSize++] = tri
        }
    }

    fun removeMergeTri(tri: TriNode) {
        if (mergeQSize == 0) {
            return
        }

        if (tri.mergeQIndex > mergeQSize - 1) {
            return
        }

        if (tri.mergeQIndex != -1) {
            // SWAP WITH LAST
            val last = mergeQ[mergeQSize - 1]
            mergeQ[tri.mergeQIndex] = last
            last!!.mergeQIndex = tri.mergeQIndex
            tri.mergeQIndex = -1
            mergeQSize--
        }
    }

    fun getAllSplitTriangles(): Array<TriNode?> {
        return splitQ.copyOfRange(0, splitQSize)
    }

    fun getAllMergeTriangles(): Array<TriNode?> {
        return mergeQ.copyOfRange(0, mergeQSize)
    }
}