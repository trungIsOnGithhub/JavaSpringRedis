// @ts-check
import React, { useEffect, useState } from "react";
import { getButtonLinks } from "../API.js";

const Navbar = () => {
  const [links, setLinks] = useState(null);
  useEffect(() => {
    getButtonLinks().then(setLinks);
  }, []);
  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white">
      <span className="navbar-brand">Redis chat demo</span>
      {links !== null ? (
        <span className="navbar-text">
          {links.github && <GithubIcon link={links.github} />}
        </span>
      ) : (
        <></>
      )}
    </nav>
  );
};

const GithubIcon = ({ link }) => (
  <a href={link} target="_blank" title="Github"></a>
);

export default Navbar;