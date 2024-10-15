package com.smartherd.boxdrop

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World
import kotlin.math.abs
import kotlin.random.Random

//class BoxDropView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null
//) : View(context, attrs) {
//
//    private val boxes = mutableListOf<Box>()
//    private val paint = Paint()
//    private var stackLimit = 8 // Set a limit for the stack height
//
//    init {
//        paint.style = Paint.Style.FILL
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        // Draw all boxes
//        for (box in boxes) {
//            paint.color = box.color
//            canvas.drawRect(box.x, box.y, box.x + box.size, box.y + box.size, paint)
//        }
//    }
//
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        if (event?.action == MotionEvent.ACTION_DOWN) {
//            // Add a new box at the top of the screen
//            val newBox = Box(event.x - 50f, 0f, randomColor(), 100f)
//            boxes.add(newBox)
//
//            // Start the drop animation for the new box
//            startDropAnimation(newBox)
//            return true
//        }
//        return super.onTouchEvent(event)
//    }
//
//    private fun startDropAnimation(box: Box) {
//        val animator = ValueAnimator.ofFloat(0f, height.toFloat() - box.size)
//        animator.duration = 2000
//        animator.addUpdateListener { animation ->
//            val newY = animation.animatedValue as Float
//
//            // Check if the box collides with the top of the previous box to stack
//            val collidingBox = getCollidingBox(box)
//            if (collidingBox != null && newY + box.size >= collidingBox.y) {
//                // Stop the animation and stack the box on top of the previous one
//                box.y = collidingBox.y - box.size
//                animator.cancel()
//
//                // Check if the stack height limit is reached
//                if (boxes.size >= stackLimit) {
//                    scatterBoxes()
//                }
//            } else {
//                // Continue dropping
//                box.y = newY
//            }
//            invalidate()
//        }
//        animator.start()
//    }
//
//    private fun getCollidingBox(box: Box): Box? {
//        // Look for a box that is directly below the current box
//        for (existingBox in boxes) {
//            if (existingBox != box &&
//                abs(existingBox.x - box.x) < box.size &&
//                box.y + box.size <= existingBox.y
//            ) {
//                return existingBox
//            }
//        }
//        return null
//    }
//
//    private fun scatterBoxes() {
//        val scatteredBoxes = boxes.toList() // Create a snapshot of current boxes for scattering
//
//        for (box in scatteredBoxes) {
//            // Apply a random scatter effect (e.g., random velocity)
//            val scatterAnimator = ValueAnimator.ofFloat(0f, 500f)
//            scatterAnimator.duration = 1000
//            val velocityX = Random.nextInt(-10, 10).toFloat()
//            val velocityY = Random.nextInt(-20, -5).toFloat()
//
//            scatterAnimator.addUpdateListener { animation ->
//                val progress = animation.animatedFraction
//                box.x += velocityX * progress
//                box.y += velocityY * progress
//                invalidate()
//            }
//            scatterAnimator.start()
//        }
//
//        // Clear only after all animations finish
//        postDelayed({
//            boxes.clear()
//        }, 1000)
//    }
//
//    private fun randomColor(): Int {
//        return Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
//    }
//
//    data class Box(var x: Float, var y: Float, val color: Int, val size: Float)
//}


class BoxDropView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val boxes = mutableListOf<Body>()
    private val paint = Paint()
    private val world: World
    private val boxSize = 50f
    private val timeStep = 1.0f / 60.0f  // 60 frames per second
    private val velocityIterations = 6
    private val positionIterations = 2
    private var groundBody: Body? = null

    init {
        // Initialize Box2D world with gravity
        world = World(Vec2(0f, 10f))  // Gravity pulling down
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Step the physics world
        world.step(timeStep, velocityIterations, positionIterations)

        // Draw all boxes
        for (box in boxes) {
            val position = box.position
            paint.color = box.userData as Int
            canvas.drawRect(
                position.x * boxSize - boxSize / 2,
                position.y * boxSize - boxSize / 2,
                position.x * boxSize + boxSize / 2,
                position.y * boxSize + boxSize / 2,
                paint
            )
        }

        // Redraw at 60 FPS
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            // Add a new box at the touch location
            val newBox = createBox(event.x / boxSize, 0f)  // Add box at the top of the screen
            boxes.add(newBox)
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun createBox(x: Float, y: Float): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.DYNAMIC
        bodyDef.position.set(x, y)
        val body = world.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(0.5f, 0.5f)  // Box size

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.density = 1.0f
        fixtureDef.friction = 0.3f
        body.createFixture(fixtureDef)

        // Assign a random color to the box
        body.userData = randomColor()

        return body
    }

    private fun randomColor(): Int {
        return Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createGround(w, h)
    }

    private fun createGround(width: Int, height: Int) {
        if (groundBody != null) {
            world.destroyBody(groundBody) // Destroy previous ground, if any
        }

        val groundBodyDef = BodyDef()
        // Position the ground at the bottom of the screen
        groundBodyDef.position.set(width / 2f / boxSize, height / boxSize)
        groundBody = world.createBody(groundBodyDef)

        val groundShape = PolygonShape()
        // Set the ground to be as wide as the screen
        groundShape.setAsBox(width.toFloat() / boxSize, 0.5f)

        groundBody!!.createFixture(groundShape, 0.0f)  // Static body, so no density
    }
}

