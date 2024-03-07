export const usePost = (baseUrl: string) => {
  return async (url: string, data: object) => {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    };
    return await fetch(baseUrl + url, requestOptions).then((resp) => resp.json());
  };
};
