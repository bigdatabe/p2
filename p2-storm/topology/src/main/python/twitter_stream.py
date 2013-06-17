#!/usr/bin/env python
"""
TO GENERATE FILE RUN "python twitter_stream.py > output_file.txt" on command line
"""

import oauth2 as oauth
import urllib2 as urllib


def twitterreq(url, http_method, parameters):
    """
    Construct, sign, and open a twitter request
    using the hard-coded credentials above.
    """
    req = oauth.Request.from_consumer_and_token(oauth_consumer,
                                                token=oauth_token,
                                                http_method=http_method,
                                                http_url=url,
                                                parameters=parameters)

    req.sign_request(signature_method_hmac_sha1, oauth_consumer, oauth_token)

    if http_method == "POST":
        encoded_post_data = req.to_postdata()
    else:
        encoded_post_data = None
        url = req.to_url()

    opener = urllib.OpenerDirector()
    opener.add_handler(http_handler)
    opener.add_handler(https_handler)

    response = opener.open(url, encoded_post_data)

    return response


def fetch_samples():
    url = "https://stream.twitter.com/1/statuses/sample.json"
    parameters = []
    response = twitterreq(url, "GET", parameters)
    for line in response:
        print line


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description="Dump Tweets to JSON")
    parser.add_argument("-a", "--accessToken", required=True)
    parser.add_argument("-s", "--accessTokenSecret", required=True)
    parser.add_argument("-c", "--consumerKey", required=True)
    parser.add_argument("-e", "--consumerSecret", required=True)
    parser.add_argument("-d", "--debug", action='store_true', default=False, required=False)
    args = parser.parse_args()

    access_token_key = ""
    access_token_secret = ""

    consumer_key = ""
    consumer_secret = ""

    oauth_token = oauth.Token(key=args.accessToken, secret=args.accessTokenSecret)
    oauth_consumer = oauth.Consumer(key=args.consumerKey, secret=args.consumerSecret)

    signature_method_hmac_sha1 = oauth.SignatureMethod_HMAC_SHA1()

    http_handler = urllib.HTTPHandler(debuglevel=args.debug)
    https_handler = urllib.HTTPSHandler(debuglevel=args.debug)

    fetch_samples()
