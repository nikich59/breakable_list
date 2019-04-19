package com.prokoshevnik.uiutils.breakablelist

import android.content.Context
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.util.SizeF
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.concurrent.atomic.AtomicInteger


class BreakableListLayout(context: Context, attrs: AttributeSet?, defStyle: Int) :
        FrameLayout(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    var isChildrenPositionFixed = false
        get() = field
        set(isChildrenPositionFixed: Boolean) {
            field = isChildrenPositionFixed
            requestLayout()
        }

    fun animateToOrder() {
        val childrenEndGeometries = getChildrenGeometryAndMargins()
        val animationEndedCount = AtomicInteger(0)
        val childCount = childCount

        for (childIndex in 0.until(childCount)) {
            val child = getChildAt(childIndex)
            val animationX = SpringAnimation(
                    child, DynamicAnimation.X, childrenEndGeometries[childIndex].x.toFloat())
            val animationY = SpringAnimation(
                    child, DynamicAnimation.Y, childrenEndGeometries[childIndex].y.toFloat())

            animationX.addEndListener({ animation, canceled, value, velocity ->
                animationEndedCount.incrementAndGet()
                if (animationEndedCount.get() == childCount) {
                    isChildrenPositionFixed = false
                }
            })
            animationY.addEndListener({ animation, canceled, value, velocity ->
                animationEndedCount.incrementAndGet()
                if (animationEndedCount.get() == childCount) {
                    isChildrenPositionFixed = false
                }
            })
            animationY.addUpdateListener { animation, value, velocity ->
                requestLayout()
            }
            animationX.start()
            animationY.start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isChildrenPositionFixed) {
            var height = 0

            for (childIndex in 0.until(childCount)) {
                val child = getChildAt(childIndex)

                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

                val childMarginBottom = (child.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
                val bottom = child.y + child.height + childMarginBottom

                if (bottom > height) {
                    height = bottom.toInt()
                }
            }

            setMeasuredDimension(widthMeasureSpec, height)

            return
        }

        var currentTopEndX = 0
        var currentTopEndY = 0

        var rowHeight = 0

        val width = MeasureSpec.getSize(widthMeasureSpec)

        for (childIndex in 0.until(childCount)) {
            val child = getChildAt(childIndex)

            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

            val childMarginLeft = (child.layoutParams as ViewGroup.MarginLayoutParams).leftMargin
            val childMarginTop = (child.layoutParams as ViewGroup.MarginLayoutParams).topMargin
            val childMarginRight = (child.layoutParams as ViewGroup.MarginLayoutParams).rightMargin
            val childMarginBottom = (child.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (currentTopEndX + childMarginLeft + childWidth + childMarginRight > width) {
                currentTopEndY += rowHeight

                currentTopEndX = childMarginLeft + childWidth + childMarginRight
                rowHeight = 0
            } else {
                currentTopEndX += childMarginLeft + childWidth + childMarginRight
            }

            if (childMarginTop + childHeight + childMarginBottom > rowHeight)
                rowHeight = childMarginTop + childHeight + childMarginBottom
        }

        setMeasuredDimension(width, currentTopEndY + rowHeight)
    }

    private class ChildGeometry(val x: Int, val y: Int, val width: Int, val height: Int,
                                val marginLeft: Int, val marginTop: Int, val marginRight: Int, val marginBottom: Int)

    private fun getChildrenGeometryAndMargins(): List<ChildGeometry> {
        val result = ArrayList<ChildGeometry>()

        var currentTopEndX = 0
        var currentTopEndY = 0

        var rowHeight = 0

        for (childIndex in 0.until(childCount)) {
            val child = getChildAt(childIndex)

            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

            val childMarginLeft = (child.layoutParams as ViewGroup.MarginLayoutParams).leftMargin
            val childMarginTop = (child.layoutParams as ViewGroup.MarginLayoutParams).topMargin
            val childMarginRight = (child.layoutParams as ViewGroup.MarginLayoutParams).rightMargin
            val childMarginBottom = (child.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (currentTopEndX + childMarginLeft + childWidth > width) {
                currentTopEndY += rowHeight

                result.add(ChildGeometry(childMarginLeft,
                        currentTopEndY + childMarginTop,
                        childMarginLeft + childWidth,
                        currentTopEndY + childMarginTop + childHeight,
                        childMarginLeft,
                        childMarginTop,
                        childMarginRight,
                        childMarginBottom)
                )

                currentTopEndX = childMarginLeft + childWidth + childMarginRight
                rowHeight = 0
            } else {
                result.add(ChildGeometry(currentTopEndX + childMarginLeft,
                        currentTopEndY + childMarginTop,
                        currentTopEndX + childMarginLeft + childWidth,
                        currentTopEndY + childMarginTop + childHeight,
                        childMarginLeft,
                        childMarginTop,
                        childMarginRight,
                        childMarginBottom)
                )

                currentTopEndX += childMarginLeft + childWidth + childMarginRight
            }

            if (childMarginTop + childHeight + childMarginBottom > rowHeight)
                rowHeight = childMarginTop + childHeight + childMarginBottom
        }

        return result
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if (isChildrenPositionFixed) {
            for (childIndex in 0.until(childCount)) {
                val child = getChildAt(childIndex)

                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            }

            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        }

        var currentTopEndX = 0
        var currentTopEndY = 0

        var rowHeight = 0

        for (childIndex in 0.until(childCount)) {
            val child = getChildAt(childIndex)

            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

            val childMarginLeft = (child.layoutParams as ViewGroup.MarginLayoutParams).leftMargin
            val childMarginTop = (child.layoutParams as ViewGroup.MarginLayoutParams).topMargin
            val childMarginRight = (child.layoutParams as ViewGroup.MarginLayoutParams).rightMargin
            val childMarginBottom = (child.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (currentTopEndX + childMarginLeft + childWidth > width) {
                currentTopEndY += rowHeight

                child.layout(childMarginLeft,
                        currentTopEndY + childMarginTop,
                        childMarginLeft + childWidth,
                        currentTopEndY + childMarginTop + childHeight)

                currentTopEndX = childMarginLeft + childWidth + childMarginRight
                rowHeight = 0
            } else {
                child.layout(currentTopEndX + childMarginLeft,
                        currentTopEndY + childMarginTop,
                        currentTopEndX + childMarginLeft + childWidth,
                        currentTopEndY + childMarginTop + childHeight)

                currentTopEndX += childMarginLeft + childWidth + childMarginRight
            }

            if (childMarginTop + childHeight + childMarginBottom > rowHeight)
                rowHeight = childMarginTop + childHeight + childMarginBottom
        }
    }
}
















