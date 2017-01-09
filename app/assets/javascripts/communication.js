import 'whatwg-fetch';

const ContentType = { JSON: 'application/json'};
const BaseUri = () => window.api.endpoint;

async function checkStatus(response) {
  if (response.ok) {
    return response;
  } else {
    var error = new Error(response.statusText);
    error.response = response;
    error.status = response.status;
    error.message = await response.text();
    throw error;
  }
}

function parseJSON(response) {
  return new Promise((resolve) => {
    response.json()
      .then((res) => {
        if(res) resolve(res);
        else resolve();
      })
      .catch(() => {
        resolve();
      });
  })
}

function handleError(error) {
  return new Promise((resolve, reject) => {
    console.error(error.message);
    reject(error);
  });
}

function fetchData(playRoute) {
  const url = playRoute.absoluteURL();
  const options = {
    method: playRoute.method,
    headers: new Headers({
      "Content-Type": ContentType,
    }),
  };
  return fetch(url, options).then(checkStatus).then(parseJSON).catch(handleError);
}

export default {
  getDataByOrigin(origin) {
    const playRoute = Router.controllers.Application.reportByOrigin(origin);
    return fetchData(playRoute);
  }
}
