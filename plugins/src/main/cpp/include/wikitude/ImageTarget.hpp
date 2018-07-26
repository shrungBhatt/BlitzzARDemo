//
//  ImageTarget.hpp
//  WikitudeUniversalSDK
//
//  Created by Andreas Schacherbauer on 12.08.17.
//  Copyright © 2017 Wikitude. All rights reserved.
//

#ifndef ImageTarget_hpp
#define ImageTarget_hpp

#ifdef __cplusplus

#include <string>
#include <functional>

#include "Geometry.hpp"


namespace wikitude { namespace sdk {

    namespace impl {


        /** @addtogroup ImageTracking
        *  @{
        */
        /** @class ImageTarget
         *  @brief A class that represents image targets that are found by an image tracker.
         */
        class Matrix4;
        class ImageTarget {
        public:
            using DistanceToTargetChangedHandler = std::function<void(int distance_, ImageTarget& firstTarget_, ImageTarget& secondTarget_)>;

        public:
            virtual ~ImageTarget() = default;
            
			/** @brief Gets the name of the associated target image in the wikitude target collection(.wtc).
			 *
			 *	@return The name of the image target.
			 */
            virtual const std::string& getName() const = 0;

			/** @brief Gets the unique id of the ImageTarget. This unique id is incremented with every recognition of the same target.
			 *
			 * @return The unique id of the image target.
			 */
            virtual long getUniqueId() const = 0;
            
            /** @brief Gets the depth factor that needs to be applied to the matrix to get real world scaling.
             *
             * Note: This getter only returns valid information in case depth camera information are passed to the SDK.
             *
             * @return The depth factor of the image target.
             */
            virtual float getDepthFactor() const = 0;

			/** @brief Gets a scale value that represents the image dimensions proportionally to the uniform scaling given through the matrix returned from getMatrix();
			 * 
			 * @return The normalized scale of the image target.
			 */
            virtual const Scale2D<float> getTargetScale() const = 0;

			/** @brief Gets the physical height of the image target as it is defined in the .wtc or through the ImageTrackerConfiguration property set with ImageTrackerConfiguration::setPhysicalTargetImageHeights()
			 *
			 * @return The physical target height in millimeter.
			 */
            virtual int getPhysicalTargetHeight() const = 0;

			/** @brief Gets the distance from the camera to the image target in millimeter.
			 *
			 * This value only contains reliable values if the .wtc file or the cloud archive included physical image target heights.
			 * @return The physical distance in millimeter between the camera and the image target.
			 */
            virtual int getDistanceToTarget() const = 0;

			/** @brief Gets the physical distance between two image targets
			 *
			 * @param otherTarget The image target to which the distance should be calculated to.
			 * @return The physical distance in millimeter between this target and imageTarget.
			 */
            virtual int getDistanceTo(const ImageTarget& otherTarget) const = 0;

            /** @brief Sets an handler to observe changes in the distance between this and other ImageTargets
             *
             */
            virtual void setDistanceToTargetChangedHandler(DistanceToTargetChangedHandler handler_) = 0;

            virtual Rectangle<int> getTargetAreaInCameraFrame() const = 0;
            virtual Rectangle<int> getTargetAreaInScreenSpace(Size<int> cameraFrameSize_, Size<int> surfaceSize_, const Matrix4& projectionMatrix_, float angle_) const = 0;

            /** @brief Gets the combined modelview matrix that should be applied to augmentations when rendering.
             * In cases where the orientation of the rendering surface and orientation of the camera do not match, and a correct cameraToRenderSurfaceRotation is passed to the SDK,
             * this matrix will also be rotate to account for the mismatch.
             *
             * For example, on mobile devices running in portrait mode, that have a camera sensor is landscape right position, the cameraToRenderSurfaceRotation should be 90 degrees.
             * The matrix will be rotated by 90 degrees around the Z axis.
             *
             * @return The matrix that should be applied to the target augmentation when rendering.
             */
            virtual const Matrix4& getMatrix() const = 0;
        };
        /** @}*/
    }    
    using impl::ImageTarget;
}}

#endif /* __cplusplus */

#endif /* ImageTarget_hpp */
