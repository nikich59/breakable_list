package com.prokoshevnik.uiutils.breakablelist

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout


class BreakableListLayout(context: Context, attrs: AttributeSet?, defStyle: Int) :
        FrameLayout(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private var isChildrenPositionFixed = false
        get() = field
        set(isChildrenPositionFixed: Boolean) {
            field = isChildrenPositionFixed
            requestLayout()
        }
    
    public var trewe = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isChildrenPositionFixed) {
            var height = 0

            for (childIndex in 0.until(childCount)) {
                val child = getChildAt(childIndex)

                if (child.bottom > height) {
                    height = child.bottom
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

            child.measure(0, 0)

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

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if (isChildrenPositionFixed) {
            return
        }

        var currentTopEndX = 0
        var currentTopEndY = 0

        var rowHeight = 0

        for (childIndex in 0.until(childCount)) {
            val child = getChildAt(childIndex)

            child.measure(0, 0)

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
















