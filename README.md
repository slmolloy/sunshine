Sunshine app created from Udacity Developing Android Apps course.

#Install
You will need to create and configure an API key for Open Weather Map.
http://openweathermap.org
Define a your API key as a system environment variable.

On Linux define a new file /etc/profile.d/openweathermapapikey.sh with the contents:
```
export OPEN_WEATHER_MAP_API_KEY="<your api key>"
```
Logout and log back in for /etc/profile.d settings to take effect.

On other systems define an environment variable using best know methods.
The gradle build will look for the OPEN_WEATHER_MAP_API_KEY environment variable.
