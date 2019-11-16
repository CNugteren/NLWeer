#!/usr/bin/env python3

# Use this file to 'compute' the 'coordinates' array for each of the maps in the NLWeer app. This is
# done by a simple brute-force search over a specified range of lon/lat/rotatation values, given a
# set of points for which the location is known (see the 'POINTS' variable) and a corresponding list
# of expected positions measured in terms of percentage of the width/height on the image.

from typing import List, Tuple

import numpy as np


POINTS = [  # points of interest in (lat, lon)
    (51.3694, 3.3659), # SW border with Belgium at Cadzand
    (50.7543, 6.0209), # SE border with Belgium & Germany at Vaalserberg
    (51.8236, 5.9449), # E border with Germany at Nijmegen
    (53.2360, 7.2098), # NE border with Germany at the bottom of the Dollart
    (53.4390, 5.5466), # N border with the sea at the eastern most point of Terschelling
    (53.1851, 4.8522), # NW border with the sea at the northern most point of Texel
    (51.9841, 4.0964), # S border with the sea at the extreme point at Hoek van Holland
]


def coord_fitter(ground_truths: List[Tuple[float, float]], limits: Tuple[float]):
    best_score = 999999
    for min_lat in np.arange(limits[0], limits[1], 0.05):
        for min_lon in np.arange(limits[2], limits[3], 0.05):
            for max_lat in np.arange(limits[4], limits[5], 0.05):
                for max_lon in np.arange(limits[6], limits[7], 0.05):
                    for angle1 in np.arange(limits[8], limits[9], 0.01):
                        for angle2 in np.arange(limits[10], limits[11], 0.01):
                            score = 0
                            for (lat, lon), (gt_width, gt_height) in zip(POINTS, ground_truths):
                                width = (lon - min_lon) / (max_lon - min_lon)
                                height = (lat - min_lat) / (max_lat - min_lat)
                                width += height * angle1
                                height += width * angle2
                                score += abs(width - gt_width) + abs(height - gt_height)
                            if score < best_score:
                                best_score = score
                                print("Score: %.4lf ==> %.2lff, %.2lff, %.2lff, %.2lff, %.2lff, %.2lff" % (
                                    score, min_lat, min_lon, max_lat, max_lon, angle1, angle2))

def knmi_radar():
    """Covers the rain-radar map from KNMI"""
    # Best fit: 50.60f, 1.85f, 54.05f, 7.20f, -0.10f, 0.09f
    ground_truths = [  # (width, height) in percentages corresponding to the above points
        (0.26, 0.25),
        (0.78, 0.11),
        (0.73, 0.42),
        (0.91, 0.85),
        (0.62, 0.88),
        (0.49, 0.79),
        (0.38, 0.44),
    ]
    limits = (
        50.3, 52.0,
        1.6, 2.2,
        53.5, 54.5,
        7.0, 7.5,
        -0.15, -0.05,
        0.0, 0.10,
    )
    print("Fitting coordinates for rain radar KNMI map...")
    coord_fitter(ground_truths, limits)


def knmi_forecast():
    """Covers all the three forecast maps from KNMI"""
    # Best fit: 50.70f, 3.10f, 53.65f, 7.55f, -0.01f, 0.00f
    ground_truths = [  # (width, height) in percentages corresponding to the above points
        (0.04, 0.23),
        (0.66, 0.02),
        (0.64, 0.38),
        (0.91, 0.85),
        (0.54, 0.93),
        (0.39, 0.84),
        (0.22, 0.44),
    ]
    limits = (
        50.3, 51.5,
        2.8, 3.3,
        53.3, 54.3,
        7.2, 7.8,
        -0.05, 0.05,
        -0.05, 0.05,
    )
    print("Fitting coordinates for forecast KNMI maps...")
    coord_fitter(ground_truths, limits)


def knmi_now():
    """Covers the 'wind' and 'temperature' maps from KNMI"""
    # Best fit: 50.70f, 2.95f, 53.65f, 7.30f, -0.01f, 0.00f
    ground_truths = [  # (width, height) in percentages corresponding to the above points
        (0.09, 0.23),
        (0.71, 0.02),
        (0.69, 0.38),
        (0.96, 0.87),
        (0.59, 0.93),
        (0.43, 0.84),
        (0.26, 0.44),
    ]
    limits = (
        50.3, 51.5,
        2.5, 3.3,
        53.3, 54.3,
        7.2, 7.8,
        -0.05, 0.05,
        -0.05, 0.05,
    )
    print("Fitting coordinates for wind/temperature KNMI maps...")
    coord_fitter(ground_truths, limits)


def main():
    # Comment out one of the below if you are only interested in a particular fit
    knmi_radar()
    knmi_forecast()
    knmi_now()


main()
